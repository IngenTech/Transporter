package weatherrisk.com.wrms.transporter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;



import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.dataobject.ContentData;
import weatherrisk.com.wrms.transporter.fragment.ContentDataFragment;

/**
 * Created by Admin on 12-05-2017.
 */
public class DetailActivity extends AppCompatActivity {

   // ContentData customerConfirmOrder;
    ArrayList<ContentData> contentDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        contentDatas = new ArrayList<ContentData>();

        Bundle getBundle = this.getIntent().getExtras();
        ArrayList<ContentData> channelsList = getBundle.getParcelableArrayList("content");
        contentDatas=channelsList;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);



        System.out.println("contentDatas size : "+contentDatas.size());

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        //TODO we need to add document data array instead of null
        transaction.replace(R.id.frameLayoutInner, ContentDataFragment.newInstance(contentDatas,null));
        //  transaction.addToBackStack(true);
        transaction.commit();

    }

    public DetailActivity(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
