package openfoodfacts.github.scrachx.openfood.features.product.view.changes_history

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.databinding.FragmentChangesHistoryProductBinding
import openfoodfacts.github.scrachx.openfood.features.search.ProductSearchActivity
import openfoodfacts.github.scrachx.openfood.features.shared.BaseFragment
import openfoodfacts.github.scrachx.openfood.models.ProductState
import openfoodfacts.github.scrachx.openfood.utils.SearchType
import openfoodfacts.github.scrachx.openfood.utils.requireProductState
import java.text.SimpleDateFormat
import java.util.*

/**
 * @see R.layout.fragment_changes_history_product
 */
class ChangesHistoryProductFragment : BaseFragment() {
    private var _binding: FragmentChangesHistoryProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var productState: ProductState

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
        } else {
            binding.creatorTxt.visibility = View.INVISIBLE
        }

        if (!product.lastModifiedBy.isNullOrBlank()) {
            val lastEditDate = getDateTime(product.lastModifiedTime!!)
            val editorTxt = getString(R.string.last_editor_history, lastEditDate.first, lastEditDate.second, product.lastModifiedBy)
            binding.lastEditorTxt.movementMethod = LinkMovementMethod.getInstance()
            binding.lastEditorTxt.text = editorTxt
        } else {
            binding.lastEditorTxt.visibility = View.INVISIBLE
        }

        if (product.editors.isNotEmpty()) {
            val otherEditorsTxt = getString(R.string.other_editors)
            binding.otherEditorsTxt.movementMethod = LinkMovementMethod.getInstance()
            binding.otherEditorsTxt.text = "$otherEditorsTxt "
            product.editors.forEach { editor ->
                binding.otherEditorsTxt.append(getContributorsTag(editor).subSequence(0, editor.length))
                binding.otherEditorsTxt.append(", ")
            }
            binding.otherEditorsTxt.append(getContributorsTag(product.editors.last()))
        } else {
            binding.otherEditorsTxt.visibility = View.INVISIBLE
        }
    }

    /**
     * Get date and time in MMMM dd, yyyy and HH:mm:ss a format
     *
     * @param dateTime date and time in miliseconds
     */
    private fun getDateTime(dateTime: String): Pair<String, String> {
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

    private fun getContributorsTag(contributor: String): CharSequence {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) = ProductSearchActivity.start(requireContext(), SearchType.CONTRIBUTOR, contributor)
        }
        return SpannableStringBuilder().apply {
            append(contributor)
            setSpan(clickableSpan, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            append(" ")
        }
    }
}