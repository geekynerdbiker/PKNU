package kr.ac.sme.pknu.imagetilegame;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    EditText rowText, columnText;   //  activity_main.xml에 선언된 두 개의 EditText를 가리키는 참조
    TextView totalTime, totalMoves;


    /**
     * 'Game Start' 버튼을 클릭에 대한 callback 함수.
     * 현재 화면에 보이는 Layout(inputLayout)을 숨기고, INVISIBLE 상태였던 TileImageView를
     * VISIBLE로 변경해서 화면에 보이게 만들어 준다.
     *
     * @param view
     */
    public void hideLayout(View view) {
        // inputLayout 요소를 화면에서 숨긴다.

        if (Integer.parseInt(rowText.getText().toString()) <= 0)
            Toast.makeText(getApplicationContext(), "행 값은 0보다 커야합니다.", Toast.LENGTH_LONG).show();

        if (Integer.parseInt(columnText.getText().toString()) <= 0)
            Toast.makeText(getApplicationContext(), "열 값은 0보다 커야합니다.", Toast.LENGTH_LONG).show();
        else {
            LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputLayout);
            inputLayout.setVisibility(View.INVISIBLE);

            // imagePanel 요소가 화면에 보이도록 VISIBLE 상태로 변경한다.
            final TileImageView imageView = (TileImageView) findViewById(R.id.imagePanel);
            imageView.setMain(this);
            imageView.setDimension(Integer.parseInt("" + rowText.getText()), Integer.parseInt("" + columnText.getText()));
            imageView.setVisibility(View.VISIBLE);

            imageView.setOnTouchListener(imageView);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    endGame();
                    return true;
                }
            });

            // 일단은 TileImageView가 보이고 300초 뒤에 게임이 자동 종료되어서 buttonPanel 요소가 화면에 나오도록 하였음.
            Handler showButtonPanel = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    imageView.setVisibility(View.INVISIBLE);  // TileImageView를 숨긴다.

                    LinearLayout buttonPanel = (LinearLayout) findViewById(R.id.buttonPanel);
                    buttonPanel.setVisibility(View.VISIBLE);
                }
            };
            showButtonPanel.postDelayed(r, 300 * 1000);  // 300초 뒤에 화면 전환됨!
        }
    }

    public void endGame() {
        final TileImageView imageView = (TileImageView) findViewById(R.id.imagePanel);

        Handler showButtonPanel = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.INVISIBLE);  // TileImageView를 숨긴다.

                LinearLayout buttonPanel = (LinearLayout) findViewById(R.id.buttonPanel);
                buttonPanel.setVisibility(View.VISIBLE);
            }
        };
        showButtonPanel.post(r);
    }

    /**
     * 현재 스크린에서 앱의 타이틀 바 영역을 제외한 실제 내용물이 그려지는 영역의 크기를 얻어준다.
     * TileImageView 클래스에서 화면 영역 내에서 터치되는 위치의 Tile을 찾거나,
     * onDraw(Canvas) 메소드로 전체 화면을 그리기 위해 호출하여 사용함.
     *
     * @return 화면 크기(width, height)를 Point 객체에 담아서 반환함.
     */
    public Point getContentSize() {
        Point size = new Point();

        // 현재 화면에 그려지는 내용 영역의 최상위 View(android.R.id.content)로부터 크기 정보를 얻음.
        size.x = findViewById(android.R.id.content).getWidth();
        size.y = findViewById(android.R.id.content).getHeight();

        return size;
    }

    public void goToMain(View view) {
        setContentView(R.layout.activity_main);

        rowText = (EditText) findViewById(R.id.rowText);
        columnText = (EditText) findViewById(R.id.columnText);
        totalTime = (TextView) findViewById(R.id.totalTime);
        totalMoves = (TextView) findViewById(R.id.totalMoves);
    }

    public void oneMoreGame(View view) {
        setContentView(R.layout.activity_main);

        totalTime = (TextView) findViewById(R.id.totalTime);
        totalMoves = (TextView) findViewById(R.id.totalMoves);

        hideLayout(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 시작하면서는 inputLayout 요소만 화면에 보임.

        // activity_main.xml 파일에서 inputLayout 내의 두 EditText를 찾아서 멤버 변수에 설정해 둠.
        rowText = (EditText) findViewById(R.id.rowText);
        columnText = (EditText) findViewById(R.id.columnText);
        totalTime = (TextView) findViewById(R.id.totalTime);
        totalMoves = (TextView) findViewById(R.id.totalMoves);
    }
}
