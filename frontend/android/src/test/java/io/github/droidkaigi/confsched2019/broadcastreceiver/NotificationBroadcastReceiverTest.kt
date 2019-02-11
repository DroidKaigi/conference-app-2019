package io.github.droidkaigi.confsched2019.broadcastreceiver

import io.github.droidkaigi.confsched2019.util.SessionAlarm.Companion.BROADCAST_RECEIVER_CLASS_NAME
import io.kotlintest.shouldBe
import org.junit.Test

class NotificationBroadcastReceiverTest {
    @Test fun shouldBroadCastReceiverSameName() {
        NotificationBroadcastReceiver::class.java.name shouldBe BROADCAST_RECEIVER_CLASS_NAME
    }
}
