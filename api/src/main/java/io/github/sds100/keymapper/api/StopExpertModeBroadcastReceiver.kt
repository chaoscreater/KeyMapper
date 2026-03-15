package io.github.sds100.keymapper.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.sds100.keymapper.base.expertmode.SystemBridgeSetupUseCase
import javax.inject.Inject

// DON'T MOVE THIS CLASS TO A DIFFERENT PACKAGE OR RENAME BECAUSE IT BREAKS THE API
@AndroidEntryPoint
class StopExpertModeBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var useCase: SystemBridgeSetupUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        if (intent?.action != Api.ACTION_STOP_EXPERT_MODE) return

        useCase.stopSystemBridge()
    }
}
