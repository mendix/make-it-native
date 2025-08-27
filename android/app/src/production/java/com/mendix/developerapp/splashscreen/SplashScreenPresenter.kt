import android.app.Activity
import com.zoontek.rnbootsplash.RNBootSplash

class SplashScreenPresenter {
    override fun show(activity: Activity) {
        RNBootSplash.init(activity)
    }

    override fun hide(activity: Activity) {
        RNBootSplash.hide(activity, true)
    }
}