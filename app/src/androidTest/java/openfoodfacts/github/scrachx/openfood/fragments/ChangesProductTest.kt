package openfoodfacts.github.scrachx.openfood.fragments

import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import openfoodfacts.github.scrachx.openfood.features.product.view.changes_history.ChangesHistoryProductFragment
import openfoodfacts.github.scrachx.openfood.utils.isNotBlank
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class ChangesProductTest {
    @Test
    fun testChangesHistoryProductFragment() {
        with(launchFragment<ChangesHistoryProductFragment>()) {
            onFragment { fragment ->
                assertNotNull(fragment.binding.creatorTxt)

            }
        }
    }
}