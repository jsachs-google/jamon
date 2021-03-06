/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Holiday {
  private Date date;
  private String name;

  static private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

  public Holiday (String p_name, Date p_date) {
    name = p_name;
    date = p_date;
  }

  public String getName() { return  name; }
  public Date getDate() { return  date; }

  public static void main(String[] argv) throws Exception {
    List<Holiday> holidays = new ArrayList<Holiday>();
    holidays.add(new Holiday("New Year's", format.parse("2003-1-1")));
    holidays.add(new Holiday("July 4", format.parse("2003-7-4")));
    holidays.add(new Holiday("Thanksgiving", format.parse("2003-11-17")));
    holidays.add(new Holiday("Christmas", format.parse("2003-12-25")));
    new FragmentExampleTemplate()
      .render(new OutputStreamWriter(System.out), holidays);
  }
}
