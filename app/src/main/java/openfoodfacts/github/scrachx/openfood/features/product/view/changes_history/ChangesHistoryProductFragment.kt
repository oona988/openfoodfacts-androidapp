package openfoodfacts.github.scrachx.openfood.features.product.view.changes_history

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.models.ProductState
import openfoodfacts.github.scrachx.openfood.utils.requireProductState
import openfoodfacts.github.scrachx.openfood.databinding.FragmentChangesHistoryProductBinding
import openfoodfacts.github.scrachx.openfood.features.shared.BaseFragment
import java.text.SimpleDateFormat
import java.util.*


open class ChangesHistoryProductFragment : BaseFragment() {
    open var _binding: FragmentChangesHistoryProductBinding? = null
    open val binding get() = _binding!!

    open lateinit var productState: ProductState

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangesHistoryProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshView(this.requireProductState())
    }

    override fun onDestroyView() {
        disp.clear()
        super.onDestroyView()
    }

    override fun refreshView(productState: ProductState) {
        super.refreshView(productState)
        this.productState = productState
        val product = this.productState.product!!

        if (!product.creator.isNullOrBlank()) {
            val createdDate = getDateTime(product.createdDateTime!!)
            val creatorTxt = getString(R.string.creator_history, createdDate.first, createdDate.second, product.creator)
            binding.creatorTxt.movementMethod = LinkMovementMethod.getInstance()
            binding.creatorTxt.text = creatorTxt
            binding.timelineStart.initLine(1)
        } else {
            binding.creatorTxt.visibility = View.INVISIBLE
        }

        if (!product.lastModifiedBy.isNullOrBlank()) {
            val lastEditDate = getDateTime(product.lastModifiedTime!!)
            val editorTxt = getString(R.string.last_editor_history, lastEditDate.first, lastEditDate.second, product.lastModifiedBy)
            binding.lastEditorTxt.movementMethod = LinkMovementMethod.getInstance()
            binding.lastEditorTxt.text = editorTxt
            binding.timelineEnd.initLine(0)
        } else {
            binding.lastEditorTxt.visibility = View.INVISIBLE
        }
    }

    /**
     * Get date and time in MMMM dd, yyyy and HH:mm:ss a format
     *
     * @param dateTime date and time in milliseconds
     */
    open fun getDateTime(dateTime: String): Pair<String, String> {
        val unixSeconds = dateTime.toLong()
        val date = Date(unixSeconds * 1000L)
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("CET")
        }
        val sdf2 = SimpleDateFormat("HH:mm:ss a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("CET")
        }
        return sdf.format(date) to sdf2.format(date)
    }
}