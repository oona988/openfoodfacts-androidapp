package openfoodfacts.github.scrachx.openfood.features.scanhistory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import openfoodfacts.github.scrachx.openfood.AppFlavors.OFF
import openfoodfacts.github.scrachx.openfood.AppFlavors.isFlavors
import openfoodfacts.github.scrachx.openfood.BuildConfig
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.databinding.ActivityHistoryScanBinding
import openfoodfacts.github.scrachx.openfood.features.listeners.CommonBottomListenerInstaller.installBottomNavigation
import openfoodfacts.github.scrachx.openfood.features.listeners.CommonBottomListenerInstaller.selectNavigationItem
import openfoodfacts.github.scrachx.openfood.features.productlist.CreateCSVContract
import openfoodfacts.github.scrachx.openfood.features.scan.ContinuousScanActivity
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity
import openfoodfacts.github.scrachx.openfood.utils.*
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper.getLocaleFromContext
import openfoodfacts.github.scrachx.openfood.utils.SortType.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScanHistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityHistoryScanBinding

    private val viewModel: ScanHistoryViewModel by viewModels()

    @Inject
    lateinit var picasso: Picasso

    /**
     * boolean to determine if menu buttons should be visible or not
     */
    private var menuButtonsEnabled = false

    private val adapter by lazy {
        ScanHistoryAdapter(isLowBatteryMode = isDisableImageLoad() && isBatteryLevelLow(), picasso) {
            openProductActivity(it.barcode)
        }
    }

    private val storagePermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            exportAsCSV()
        } else {
            MaterialDialog.Builder(this)
                    .title(R.string.permission_title)
                    .content(R.string.permission_denied)
                    .negativeText(R.string.txtNo)
                    .positiveText(R.string.txtYes)
                    .onPositive { _, _ ->
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", this@ScanHistoryActivity.packageName, null)
                        })
                    }
                    .onNegative { dialog, _ -> dialog.dismiss() }
                    .show()
        }
    }

    private val cameraPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            openContinuousScanActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    val fileWriterLauncher = registerForActivityResult(CreateCSVContract()) {
        writeHistoryToFile(this, adapter.products, it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.getBoolean(R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        binding = ActivityHistoryScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.scan_history_drawer)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.listHistoryScan.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listHistoryScan.adapter = adapter
        val swipeController = SwipeController(this) { position ->
            adapter.products.getOrNull(position)?.let {
                viewModel.removeProductFromHistory(it)
            }
        }
        ItemTouchHelper(swipeController).attachToRecyclerView(binding.listHistoryScan)

        binding.scanFirst.setOnClickListener { startScan() }
        binding.srRefreshHistoryScanList.setOnRefreshListener { viewModel.refreshItems() }
        binding.navigationBottom.bottomNavigation.installBottomNavigation(this)

        viewModel.observeFetchProductState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeLifecycle(this) { state ->
                    when (state) {
                        is ScanHistoryViewModel.FetchProductsState.Data -> {
                            binding.srRefreshHistoryScanList.isRefreshing = false
                            binding.historyProgressbar.isVisible = false

                            adapter.products = state.items

                            if (state.items.isEmpty()) {
                                setMenuEnabled(false)
                                binding.emptyHistoryInfo.isVisible = true
                                binding.scanFirst.isVisible = true
                            } else {
                                setMenuEnabled(true)
                            }

                            adapter.notifyDataSetChanged()
                        }
                        ScanHistoryViewModel.FetchProductsState.Error -> {
                            setMenuEnabled(false)
                            binding.srRefreshHistoryScanList.isRefreshing = false
                            binding.historyProgressbar.isVisible = false
                            binding.emptyHistoryInfo.isVisible = true
                            binding.scanFirst.isVisible = true
                        }
                        ScanHistoryViewModel.FetchProductsState.Loading -> {
                            setMenuEnabled(false)
                            if (binding.srRefreshHistoryScanList.isRefreshing.not()) {
                                binding.historyProgressbar.isVisible = true
                            }
                        }
                    }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        with(menu) {
            val alpha = if (menuButtonsEnabled) 255 else 130
            findItem(R.id.action_export_all_history).isEnabled = menuButtonsEnabled
            findItem(R.id.action_export_all_history).icon.alpha = alpha

            findItem(R.id.action_remove_all_history).isEnabled = menuButtonsEnabled
            findItem(R.id.action_remove_all_history).icon.alpha = alpha

            findItem(R.id.sort_history).isEnabled = menuButtonsEnabled
            findItem(R.id.sort_history).icon.alpha = alpha
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        R.id.action_remove_all_history -> {
            showDeleteConfirmationDialog()
            true
        }
        R.id.action_export_all_history -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    MaterialDialog.Builder(this).run {
                        title(R.string.action_about)
                        content(R.string.permision_write_external_storage)
                        positiveText(R.string.txtOk)
                        onPositive { _, _ ->
                            storagePermLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                        show()
                    }
                } else {
                    storagePermLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                exportAsCSV()
            }
            true
        }
        R.id.sort_history -> {
            showListSortingDialog()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        binding.navigationBottom.bottomNavigation.selectNavigationItem(R.id.history_bottom_nav)
    }

    private fun setMenuEnabled(enabled: Boolean) {
        menuButtonsEnabled = enabled
        invalidateOptionsMenu()
    }

    private fun openProductActivity(barcode: String) {
        viewModel.openProduct(barcode, this)
    }

    private fun openContinuousScanActivity() {
        Intent(this, ContinuousScanActivity::class.java)
                .apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
                .also { startActivity(it) }
    }

    private fun exportAsCSV() {
        Toast.makeText(this, R.string.txt_exporting_history, Toast.LENGTH_LONG).show()

        val flavor = BuildConfig.FLAVOR.toUpperCase(Locale.ROOT)
        val date = SimpleDateFormat("yyyy-MM-dd", getLocaleFromContext(this)).format(Date())
        val fileName = "$flavor-history_$date.csv"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fileWriterLauncher.launch(fileName)
        } else {
            val baseDir = File(Environment.getExternalStorageDirectory(), getCsvFolderName())
            if (!baseDir.exists()) baseDir.mkdirs()
            val file = File(baseDir, fileName)
            writeHistoryToFile(this, adapter.products, file.toUri())
        }
    }

    private fun startScan() {
        if (!isHardwareCameraInstalled(baseContext)) return
        val perm = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(baseContext, perm) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                MaterialDialog.Builder(this)
                        .title(R.string.action_about)
                        .content(R.string.permission_camera)
                        .positiveText(R.string.txtOk)
                        .onPositive { _, _ -> cameraPermLauncher.launch(perm) }
                        .show()
            } else {
                cameraPermLauncher.launch(perm)
            }
        } else {
            openContinuousScanActivity()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialDialog.Builder(this)
                .title(R.string.title_clear_history_dialog)
                .content(R.string.text_clear_history_dialog)
                .onPositive { _, _ -> viewModel.clearHistory() }
                .positiveText(R.string.txtYes)
                .negativeText(R.string.txtNo)
                .show()
    }

    private fun showListSortingDialog() {
        val sortTypes = if (isFlavors(OFF)) arrayOf(
                getString(R.string.by_title),
                getString(R.string.by_brand),
                getString(R.string.by_nutrition_grade),
                getString(R.string.by_barcode),
                getString(R.string.by_time)
        ) else arrayOf(
                getString(R.string.by_title),
                getString(R.string.by_brand),
                getString(R.string.by_time),
                getString(R.string.by_barcode)
        )
        MaterialDialog.Builder(this)
                .title(R.string.sort_by)
                .items(*sortTypes)
                .itemsCallback { _, _, position, _ ->
                    val newType = when (position) {
                        0 -> TITLE
                        1 -> BRAND
                        2 -> if (isFlavors(OFF)) GRADE else TIME
                        3 -> BARCODE
                        else -> TIME
                    }
                    viewModel.updateSortType(newType)
                }
                .show()
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, ScanHistoryActivity::class.java))
        val LOG_TAG = ScanHistoryActivity::class.simpleName
    }

}
