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
            binding.creatorDate.text = getDate(product.createdDateTime!!)
            val creatorText = getString(R.string.changes_history_created, product.creator)
            binding.creatorText.movementMethod = LinkMovementMethod.getInstance()
            binding.creatorText.text = creatorText
        } else {
            binding.creatorDate.visibility = View.INVISIBLE
            binding.creatorText.visibility = View.INVISIBLE
        }

        if (!product.lastModifiedBy.isNullOrBlank()) {
            binding.editorDate.text = getDate(product.lastModifiedTime!!)
            val editorText = getString(R.string.changes_history_edited, product.lastModifiedBy)
            binding.editorText.movementMethod = LinkMovementMethod.getInstance()
            binding.editorText.text = editorText
            binding.timelineStart.initLine(1)
            binding.timelineEnd.initLine(0)
        } else {
            binding.timelineStart.visibility = View.INVISIBLE
            binding.timelineEnd.visibility = View.INVISIBLE
            binding.editorDate.visibility = View.INVISIBLE
            binding.editorText.visibility = View.INVISIBLE
        }
    }

    /**
     * Get date in MMMM dd, yyyy format
     *
     * @param dateTime date and time in milliseconds
     */
    open fun getDate(dateTime: String): String {
        val unixSeconds = dateTime.toLong()
        val date = Date(unixSeconds * 1000L)
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("CET")
        }
        return sdf.format(date)
    }
}