package io.github.sds100.keymapper.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.sds100.keymapper.base.BaseMainActivity

// DON'T MOVE THIS CLASS TO A DIFFERENT PACKAGE OR RENAME BECAUSE IT BREAKS THE API
class StartExpertModeSetupBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        if (intent?.action != Api.ACTION_START_EXPERT_MODE_SETUP) return

        val activityIntent = Intent(BaseMainActivity.ACTION_START_SYSTEM_BRIDGE).apply {
            setPackage(context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(activityIntent)
    }
}
