
package org.jcb.tools;
 
import java.util.*;
 
 
public class CalendarResourceBundle extends ListResourceBundle {
 
        public Object[][] getContents() {
                return contents;
        }
 
        private Object[][] contents = {
 
{ "month1", "january" },
{ "month2", "february" },
{ "month3", "march" },
{ "month4", "april" },
{ "month5", "may" },
{ "month6", "june" },
{ "month7", "july" },
{ "month8", "august" },
{ "month9", "september" },
{ "month10", "october" },
{ "month11", "november" },
{ "month12", "december" },

{ "first-day-of-week", "0" },

{ "weekDay0", "Sun" },
{ "weekDay1", "Mon" },
{ "weekDay2", "Tue" },
{ "weekDay3", "Wed" },
{ "weekDay4", "Thu" },
{ "weekDay5", "Fri" },
{ "weekDay6", "Sat" },

	};
}

