package openfoodfacts.github.scrachx.openfood.features.product.view.changes_history

import android.os.Bundle
import android.view.View
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.features.product.view.contributors.ContributorsFragment
import openfoodfacts.github.scrachx.openfood.models.ProductState
import openfoodfacts.github.scrachx.openfood.utils.requireProductState

/**
 * @see R.layout.fragment_contributors
 */
class ChangesHistoryProductFragment : ContributorsFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statesTagsCv.visibility = View.INVISIBLE
        refreshView(this.requireProductState())
    }

    override fun refreshView(productState: ProductState) {
        super.refreshView(productState)
        this.productState = productState
        val product = this.productState.product!!

        showCreatorInformation(product)
        showLastEditInformation(product)
        showOtherEditorsInformation(product)
    }
}