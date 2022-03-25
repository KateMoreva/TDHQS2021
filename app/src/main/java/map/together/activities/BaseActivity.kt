package map.together.activities

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.facebook.drawee.backends.pipeline.Fresco
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.db.AppDatabase
import map.together.lifecycle.AppMap
import map.together.lifecycle.Router
import map.together.repository.CurrentUserRepository
import map.together.utils.logger.Logger

@InternalCoroutinesApi
abstract class BaseActivity : AppCompatActivity() {

    var router: Router? = null
        private set

    var database: AppDatabase? = null
        private set

    var userId: Long? = null
        private set

    val taskContainer: CompositeDisposable = CompositeDisposable()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        Logger.d(this, "onCreate")

        AppMap.initialize()
        router = Router(this)
        database = AppDatabase.getInstance(applicationContext)

        userId = CurrentUserRepository.currentUser.value?.id


        setContentView(getActivityLayoutId())
    }

    override fun onDestroy() {
        super.onDestroy()
        taskContainer.dispose()
    }

    protected abstract fun getActivityLayoutId(): Int

    protected open fun isBottomNavVisible(): Boolean = true
}