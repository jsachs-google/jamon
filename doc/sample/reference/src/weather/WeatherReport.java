/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package weather;

public class WeatherReport
{
    public WeatherReport(String p_day, String p_city)
    {
        m_day = p_day;
        m_city = p_city;
    }

    public String getCity() { return m_city; }
    public String getDay() { return m_day; }

    private final String m_day;
    private final String m_city;
}
