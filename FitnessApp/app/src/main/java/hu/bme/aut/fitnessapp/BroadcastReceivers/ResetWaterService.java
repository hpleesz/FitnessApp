package hu.bme.aut.fitnessapp.BroadcastReceivers;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ResetWaterService extends IntentService {

    public ResetWaterService() {
        super("ResetWaterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            double water2 = 0.0;
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(calendar.YEAR);
            int month = calendar.get(calendar.MONTH);
            int day = calendar.get(calendar.DAY_OF_MONTH);

            calendar.set(year, month, day, 0,0,0);
            long today = calendar.getTimeInMillis() / 1000;



        }
    }
}
