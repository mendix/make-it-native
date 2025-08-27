import android.app.Activity
import com.zoontek.rnbootsplash.RNBootSplash
import com.mendix.developerapp.R

class SplashScreenPresenter {
    fun show(activity: Activity) {
        RNBootSplash.init(activity, R.style.BootTheme)
    }

    fun hide(activity: Activity) {
        // RNBootSplash.hide() should be called from JS/TS code
    }
}