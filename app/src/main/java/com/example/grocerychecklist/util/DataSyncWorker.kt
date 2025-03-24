import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.grocerychecklist.data.AppDatabase
import com.example.grocerychecklist.util.DatabaseSyncUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataSyncWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val downloadUrl = DatabaseSyncUtils.uploadDatabase(
                applicationContext,
                AppDatabase.getDatabase(applicationContext)
            )

            if (downloadUrl != null) {
                Log.d("DataSyncWorker", "Database upload successful. Download URL: $downloadUrl")
                Result.success()
            } else {
                Log.e("DataSyncWorker", "Database upload failed.")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DataSyncWorker", "Database upload encountered an exception.", e)
            Result.failure()
        }
    }
}