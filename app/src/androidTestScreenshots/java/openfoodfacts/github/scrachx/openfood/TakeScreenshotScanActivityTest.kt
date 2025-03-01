package openfoodfacts.github.scrachx.openfood

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import openfoodfacts.github.scrachx.openfood.features.scan.ContinuousScanActivity
import openfoodfacts.github.scrachx.openfood.test.ScreenshotActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Take screenshots...
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TakeScreenshotScanActivityTest : AbstractScreenshotTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Rule
    var activityRule = ScreenshotActivityTestRule(ContinuousScanActivity::class.java, context = context)

    @Test
    fun testTakeScreenshotScanActivity() {
        activityRule.afterActivityLaunchedAction = { screenshotActivityTestRule ->
            try {
                screenshotActivityTestRule.runOnUiThread {
                    val barcode = screenshotActivityTestRule.screenshotParameter!!.productCodes[0]
                    screenshotActivityTestRule.activity!!.showProduct(barcode)
                }
                Thread.sleep(MS_TO_WAIT_TO_DISPLAY_PRODUCT_IN_SCAN.toLong())
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
        startForAllLocales(rules = listOf(activityRule), context = context)
    }

    companion object {
        const val MS_TO_WAIT_TO_DISPLAY_PRODUCT_IN_SCAN = 2000
    }
}