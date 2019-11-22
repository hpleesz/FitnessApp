package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.Calendar;
import java.util.List;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class PublicLocationAdapterModel {

    private List<PublicLocation> items;

    public PublicLocationAdapterModel(List<PublicLocation> items) {
        this.items = items;
    }



    public String gymOpenText(int position) {
        Calendar now = Calendar.getInstance();
        //Sunday = 1
        int day = now.get(Calendar.DAY_OF_WEEK);
        if(day == 1) day = 6;
        else day = day - 2;

        int time = now.get(Calendar.HOUR_OF_DAY) * 100 + now.get(Calendar.MINUTE);

        int openTime;
        int closeTime;

        String open = items.get(position).open_hours.get(day)[0].replace(":", "");
        open = open.replaceAll("^0+", "");
        if(open.equals("")) openTime = 0;
        else openTime = Integer.parseInt(open);

        String close = items.get(position).open_hours.get(day)[1].replace(":", "");
        close = close.replaceAll("^0+", "");
        if(close.equals("")) closeTime = 0;
        else closeTime = Integer.parseInt(close);

        String textView = "";

        if(time >= openTime && time <= closeTime) textView = "Open (-" + items.get(position).open_hours.get(day)[1] + ")";
        else textView = "Closed";

        return textView;
    }

    public List<PublicLocation> getItems() {
        return items;
    }
}
