/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.operator.scalar.timestamptz;

import io.prestosql.spi.connector.ConnectorSession;
import io.prestosql.spi.function.LiteralParameters;
import io.prestosql.spi.function.ScalarOperator;
import io.prestosql.spi.function.SqlType;
import io.prestosql.spi.type.LongTimestampWithTimeZone;
import io.prestosql.spi.type.StandardTypes;
import org.joda.time.chrono.ISOChronology;

import static io.prestosql.operator.scalar.DateTimeFunctions.valueToSessionTimeZoneOffsetDiff;
import static io.prestosql.spi.function.OperatorType.CAST;
import static io.prestosql.spi.type.DateTimeEncoding.unpackMillisUtc;
import static io.prestosql.spi.type.DateTimeEncoding.unpackZoneKey;
import static io.prestosql.spi.type.TimeZoneKey.getTimeZoneKey;
import static io.prestosql.type.DateTimes.roundToEpochMillis;
import static io.prestosql.util.DateTimeZoneIndex.getChronology;
import static io.prestosql.util.DateTimeZoneIndex.getDateTimeZone;

@ScalarOperator(CAST)
public final class TimestampWithTimeZoneToTimeCast
{
    private TimestampWithTimeZoneToTimeCast() {}

    @LiteralParameters("p")
    @SqlType(StandardTypes.TIME)
    public static long cast(ConnectorSession session, @SqlType("timestamp(p) with time zone") long packedEpochMillis)
    {
        return convert(session, getChronology(unpackZoneKey(packedEpochMillis)), unpackMillisUtc(packedEpochMillis));
    }

    @LiteralParameters("p")
    @SqlType(StandardTypes.TIME)
    public static long cast(ConnectorSession session, @SqlType("timestamp(p) with time zone") LongTimestampWithTimeZone timestamp)
    {
        return convert(session, getChronology(getTimeZoneKey(timestamp.getTimeZoneKey())), roundToEpochMillis(timestamp));
    }

    private static long convert(ConnectorSession session, ISOChronology chronology, long epochMillis)
    {
        if (session.isLegacyTimestamp()) {
            int millis = chronology.millisOfDay().get(epochMillis) - chronology.getZone().getOffset(epochMillis);
            millis -= valueToSessionTimeZoneOffsetDiff(epochMillis, getDateTimeZone(session.getTimeZoneKey()));
            return millis;
        }

        long millis = chronology.getZone().convertUTCToLocal(epochMillis);
        return ISOChronology.getInstanceUTC().millisOfDay().get(millis);
    }
}
