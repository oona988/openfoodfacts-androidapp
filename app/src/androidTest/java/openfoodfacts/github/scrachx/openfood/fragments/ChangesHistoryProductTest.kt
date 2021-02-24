package openfoodfacts.github.scrachx.openfood.fragments
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertTrue
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.features.product.view.changes_history.ChangesHistoryProductFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangesHistoryProductTest {
    @Test
    fun testFragmentAddition() {
        val scenario = launchFragmentInContainer<ChangesHistoryProductFragment>(null, R.style.OFFTheme, Lifecycle.State.CREATED)
        scenario.onFragment { fragment ->
            assertTrue(fragment.isAdded)
        }
    }

    @Test
    fun testGetDate() {
        val unixTimestamp = "1614163464"
        val scenario = launchFragmentInContainer<ChangesHistoryProductFragment>(null, R.style.OFFTheme, Lifecycle.State.CREATED)
        scenario.onFragment { fragment ->
            val dateTimestamp = fragment.getDate(unixTimestamp)
            assertTrue(dateTimestamp == "February 24, 2021")
        }
    }
}