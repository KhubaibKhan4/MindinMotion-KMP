import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.mind.app.App
import org.mind.app.di.appModule
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
fun initKoin(){
    startKoin {
        modules(appModule)
    }
}