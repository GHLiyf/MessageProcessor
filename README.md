# MessageProcessor

导入
dependencies {
    implementation 'com.github.GHLiyf:MessageProcessor:1.1'
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}


Demo

public class MainActivity extends MPActivity {

    public static boolean ing = true;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.mytest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (ing) {
                    try {
                        String res = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        SendMessage(MainActivity.RECEIVERMESSAGE, 5555, res);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ing = false;
    }

    @Override
    public void ReceiveMessage(int what, Object obj) {
        switch (what) {
            case 5555:
                String str = (String) obj;
                tv.setText(str);
                break;
        }
    }


}










