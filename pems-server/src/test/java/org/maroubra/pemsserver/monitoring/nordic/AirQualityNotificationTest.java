package org.maroubra.pemsserver.monitoring.nordic;

import com.google.common.primitives.Ints;
import io.reactivex.processors.ReplayProcessor;
import org.junit.Test;
import org.maroubra.pemsserver.monitoring.SensorLog;

import static com.google.common.truth.Truth.assertThat;

public class AirQualityNotificationTest {

    @Test
    public void producesValidSensorLog() {
        int eCO2 = 400;
        byte[] eCO2bytes = Ints.toByteArray(eCO2);
        int TVOC = 121;
        byte[] TVOCbytes = Ints.toByteArray(TVOC);

        Thingy52SensorConfig config = new Thingy52SensorConfig("some-temp-id");
        ReplayProcessor<SensorLog> processor = ReplayProcessor.create(10);

        AirQualityNotification notification = new AirQualityNotification(config, processor);
        // Typically we would have to watch for signing of bytes here, but we're within the range so its okay.
        notification.run(new byte[] { eCO2bytes[3], eCO2bytes[2], TVOCbytes[3], TVOCbytes[2] });

        SensorLog createdLog = processor.blockingFirst();

        assertThat(createdLog).isNotNull();
        assertThat(createdLog.getSensorId()).matches(config.id());
        assertThat(createdLog.getAttributeValue()).containsExactly(AirQualityNotification.CO2_VALUE_ID, eCO2, AirQualityNotification.TVOC_VALUE_ID, TVOC);
    }
}
