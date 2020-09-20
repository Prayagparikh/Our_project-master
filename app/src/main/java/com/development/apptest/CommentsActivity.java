package com.development.apptest;

import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.development.apptest.Adapter.CommentAdapter;
import com.development.apptest.Model.Comment;
import com.development.apptest.Model.User;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;



    private Button btn_Ana ;

    private static String sentiment="";
    private static String senti="";

    private static String posi;
    private static String negi ;
    private static String  neu ;
    private static String tot ;

    private static int positive;
    private static int negative;
    private static int neutral;
    private static int total;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;
    private class AskSentimentTask extends AsyncTask<String, Void, String> {

        //public String sentiment="";
        CommentsActivity ca;
        @Override
        protected String doInBackground(String... urls) {

            ca.sentiment = "";
            ca.senti=""; // without percentage

            String result="";
            URL url;
            HttpURLConnection urlConnection;

            System.out.println(sentiment);

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    textView.setText("Working...!!");
//                }
//            });

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream is = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(is);

                int data = reader.read();

                while(data!=-1) {

                    char current =(char)data;
                    result+=current;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //sentiment = "text sentimet";
            System.out.println(result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {

                JSONObject jsonObject = new JSONObject(result);
                String sentimentPos = jsonObject.getString("positive");
                String sentimentNeg = jsonObject.getString("negative");

                double pos = Double.parseDouble(sentimentPos);
                double neg = Double.parseDouble(sentimentNeg);

                DecimalFormat df = new DecimalFormat("##.##");
                double dif = pos-neg;

                if(dif > 0.1) {

                    ca.sentiment += ("Positive with: " + df.format(pos*100)+ "%").toString();
                    ca.senti += "Positive";
                }

                else if (neg >= 0.59)  {

                    ca.sentiment += ("Negative with: " + df.format(neg*100) + "%").toString();
                    ca.senti += "Negative";
                }
                else if( dif < 0.12 ) {

                    ca.sentiment += "Neutral".toString();
                    ca.senti += "Neutral";
                }
                //textView.setText("Positive: "+sentimentPos+"\nNegative: "+sentimentNeg);
                System.out.println(ca.sentiment);
                System.out.println("Senti"+ca.senti);


                addComment(ca.sentiment);
                //updatePost(ca.senti);


                ca.positive=0;
                ca.negative=0;
                ca.neutral=0;
                ca.total=0;

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       ca.posi = dataSnapshot.child("pos").getValue().toString();
                       ca.negi = dataSnapshot.child("neg").getValue().toString();
                       ca.neu = dataSnapshot.child("neu").getValue().toString();
                       ca.tot = dataSnapshot.child("totalComment").getValue().toString();


                        System.out.println("Positive Retrived: "+ ca.posi);
                        System.out.println("Negative: "+ ca.negi);
                        System.out.println("Neutral: "+ ca.neu);
                        System.out.println("Total Comments: "+ ca.tot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                System.out.println("Positive Converted"+ ca.posi);
                System.out.println("Negative: "+ ca.negi);
                System.out.println("Neutral: "+ ca.neu);
                System.out.println("Total Comments: "+ ca.tot);
                ca.total = Integer.parseInt(ca.tot)+1;

                ca.positive = Integer.parseInt(ca.posi);
                ca.negative = Integer.parseInt(ca.negi);
                ca.neutral = Integer.parseInt(ca.neu);

                if(ca.senti.equals("Positive"))
                    ca.positive++;

                else if(ca.senti.equals("Negative"))
                    ca.negative++;

                else
                    ca.neutral++;


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

                Map<String, Object> updatedValue = new HashMap<>();
                updatedValue.put("/pos", ca.positive);
                updatedValue.put("/neg", ca.negative);
                updatedValue.put("/neu", ca.neutral);
                updatedValue.put("/totalComment", ca.total);

                reference.updateChildren(updatedValue);

                System.out.println("Positive after Updated: "+ ca.positive);
                System.out.println("Negative12: "+ ca.negative);
                System.out.println("Neutral: "+ ca.neutral);
                System.out.println("Total Comments: "+ ca.total);

                System.out.print(ca.senti);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void findSentiment() {

        try {
            final String emo_regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
            String []sent = addcomment.getText().toString().split("");
            for(String word : sent)
                System.out.println(word);
            String mainComment ="";
            for (String word : sent) {
                if (!word.matches(emo_regex)) {
                    mainComment+=word;
                }
            }
            System.out.println(mainComment);

            String encodeWord = URLEncoder.encode(mainComment.toString(), "UTF-8");

            AskSentimentTask task = new AskSentimentTask();

            task.execute("https://api.uclassify.com/v1/uClassify/Sentiment/classify/?readKey=2L56yKfVaext&text=" + encodeWord);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        btn_Ana = (Button) findViewById(R.id.btn_ana);

       /* btn_Ana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String pos = dataSnapshot.child("pos").getValue().toString();
                        String neg = dataSnapshot.child("neg").getValue().toString();
                        String neu = dataSnapshot.child("neu").getValue().toString();
                        String tot = dataSnapshot.child("totalComment").getValue().toString();
                        System.out.println("Positive: "+ pos);
                        System.out.println("Negative: "+ neg);
                        System.out.println("Neutral: "+ neu);
                        System.out.println("Total Comments: "+ pos);
                        Toast.makeText(getApplicationContext(),"Total Positive Comments: "+ pos+"\n"+"Total Negative Comments: "+ neg+"\n"+"Total Neutral Comments: "+ neu,Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });*/
        btn_Ana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String pos = dataSnapshot.child("pos").getValue().toString();
                        String neg = dataSnapshot.child("neg").getValue().toString();
                        String neu = dataSnapshot.child("neu").getValue().toString();
                        String tot = dataSnapshot.child("totalComment").getValue().toString();
                        System.out.println("Positive: "+ pos);
                        System.out.println("Negative: "+ neg);
                        System.out.println("Neutral: "+ neu);
                        System.out.println("Total Comments: "+ pos);

                        if( tot.equals("0") ) {
                            Toast.makeText(getApplicationContext(),"No comments on post", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
                            intent.putExtra("pos", pos);
                            intent.putExtra("neg", neg);
                            intent.putExtra("neu", neu);
                            intent.putExtra("tot", tot);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);

        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    findSentiment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addComment(String text){

        DatabaseReference reference11 = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference11.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("Sentiment", text);
        hashMap.put("commentid", commentid);
        System.out.println(text);
        reference11.child(commentid).setValue(hashMap);
        addNotification();
        addcomment.setText("");

    }

    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: "+addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
