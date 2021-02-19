package openfoodfacts.github.scrachx.openfood.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.features.product.view.changes_history.ChangesHistoryProductFragment
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangesProductTest {
    @Ignore
    @Test
    fun testChangesHistoryProductFragment() {
        val scenario = launchFragmentInContainer<ChangesHistoryProductFragment>(null, R.style.OFFTheme, Lifecycle.State.CREATED)
    }
}