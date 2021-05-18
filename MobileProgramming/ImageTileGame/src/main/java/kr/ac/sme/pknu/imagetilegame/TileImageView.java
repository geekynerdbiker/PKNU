package kr.ac.sme.pknu.imagetilegame;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class TileImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {
    private MainActivity mActivity = null;
    private int row;
    private int column;
    private Bitmap originalImage;
    private int tileImageWidth, tileImageHeight;
    private ArrayList<Tile> gameTiles = new ArrayList<Tile>();  // 전체 Tile들을 담아 놓는 공간

    private boolean isWin = false;
    public int totalMoves = 0;
    public long totalTime = 0;
    public long startTime = System.currentTimeMillis();


    /**
     * drawable 폴더 밑에 있는 gosh_sunflower.png 파일을 조각낸 타일 이미지와 함께,
     * 해당 타일 이미지의 (행, 열) 위치값을 저장하는 내부 클래스이다.
     */
    private class Tile {
        Bitmap image;    // 조각난 이미지
        int original_x, original_y;   // 원래 이미지에서의 x, y 위치값
        int current_x, current_y;     // 무작위로 섞여서 화면에 보이는 현재의 x, y 위치값
        boolean isLastTile;           // 원본 이미지의 오른쪽 아래 위치의 Tile이면 true가 됨.

        Tile(int x, int y, Bitmap i) {
            original_x = x;
            original_y = y;
            image = i;
        }

        /**
         * 현재 Tile 즉, 자신이 원래 이미지의 정상 위치에 있다면 true를 반환함.
         * <p>
         * 게임이 종료할려면, 플레이어가 전체 Tile들의 위치를 제대로 맞춰야 하므로,
         * 모든 Tile들에 대해서 isCorrectPosition() 함수를 호출해서 전부 true를 반환하는지 확인하면 된다.
         *
         * @return 현재 Tile이 원래 이미지의 정상 위치에 있다면 true를 반환함. 아니면 false를 반환함.
         */
        boolean isCorrectPosition() {
            return (original_x == current_x) && (original_y == current_y);
        }
    }

    public TileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // drawable 폴더의 gogh_sunflower.png 원본 파일을 읽어 들인 다음,
        // XML에서 row와 column의 값을 읽어와서 초기화한다.
        originalImage = BitmapFactory.decodeResource(getResources(), R.drawable.gogh_sunflower);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TileImageView, 0, 0);
        try {
            row = a.getInteger(R.styleable.TileImageView_row, 4);
            column = a.getInteger(R.styleable.TileImageView_column, 3);
            tileImageWidth = originalImage.getWidth() / column;
            tileImageHeight = originalImage.getHeight() / row;

            prepareTiles();
        } finally {
            a.recycle();
        }

        // 터치 이벤트의 처리를 활성화시킨다.
        setFocusable(true);
        setOnTouchListener(this);
    }

    /**
     * 사용자가 화면에서 터치하는 좌표(x, y)를 받아서, 해당 위치의 Tile 좌표를 구해서 반환한다.
     * 결과는 Tile의 행(row)과 열(column)을 크기가 2인 int[]에 담아서 반환함.
     *
     * @param x 화면에서 터치되는 x 좌표
     * @param y 화면에서 터치되는 y 좌표
     * @return 터치된 위치에 놓여 있는 Tile의 행과 열 값을 담은 int[].
     */
    private int[] findTilePositionOnScreen(int x, int y) {
        Point p = mActivity.getContentSize();  // 화면이 그려지는 영역의 크기를 얻어 옴.
        int i = y / (p.y / row);     // Tile의 높이값으로 y좌표를 나누면 몇 번째 행의 Tile인지 알 수 있음.
        int j = x / (p.x / column);  // Tile의 두께값으로 x좌표를 나누면 몇 번째 열의 Tile인지 알 수 있음.

        // length = 2인 정수 배열에 담아서, { 행, 열 }의 값을 반환
        return new int[]{i, j};
    }

    /**
     * 화면에서 터치된 Tile의 위치(행, 열)로 해당 Tile의 gameTiles 목록 내의 위치를 index 값으로 반환함.
     *
     * @param tileRow    화면에서 터치된 Tile의 행
     * @param tileColumn 화면에서 터치된 Tile의 열
     * @return gameTiles 목록 내에서 해당 Tile의 위치: 0 ~ size-1.
     */
    private int findTilePositionOnList(int tileRow, int tileColumn) {
        return (tileRow * column) + tileColumn;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int[] location = findTilePositionOnScreen(x, y);
            int index = findTilePositionOnList(location[0], location[1]);
            Tile selectedTile = gameTiles.get(index);

            // 터치된 위치의 좌표가 Tile 상에서 몇행, 몇열에 해당하는지 Toast 메시지로 보여준다.
            Toast.makeText(mActivity.getApplicationContext(),
                    "You clicked (" + location[0] + ", " + location[1] + ") tile. List Index: " + index, Toast.LENGTH_LONG).show();

            if (selectedTile.isLastTile) {
                Log.i("[TileImageView.onTouch]", "Nonsense, you selected empty space!");
                return false;

            } else {  // 이제 selectedTile의 상하좌우에 빈 타일(isLastTile == true)이 있다면, 서로 자리를 바꾸어 주도록 해야 함.
                int[] dX = {0, 0, 1, -1};
                int[] dY = {1, -1, 0, 0};

                for (int dir = 0; dir < 4; dir++) {
                    int[] tmpLocation = {location[0] + dX[dir], location[1] + dY[dir]};
                    if (tmpLocation[0] < 0 || tmpLocation[1] < 0 || tmpLocation[0] >= row || tmpLocation[1] >= column)
                        continue;
                    int tmpIndex = findTilePositionOnList(tmpLocation[0], tmpLocation[1]);
                    Tile nearTile = gameTiles.get(tmpIndex);

                    if (nearTile.isLastTile) {
                        int tmpX = gameTiles.get(index).current_x;
                        int tmpY = gameTiles.get(index).current_y;

                        gameTiles.get(index).current_x = gameTiles.get(tmpIndex).current_x;
                        gameTiles.get(index).current_y = gameTiles.get(tmpIndex).current_y;

                        gameTiles.get(tmpIndex).current_x = tmpX;
                        gameTiles.get(tmpIndex).current_y = tmpY;

                        gameTiles.add(index, nearTile);
                        gameTiles.remove(index + 1);

                        gameTiles.add(tmpIndex, selectedTile);
                        gameTiles.remove(tmpIndex + 1);
                        invalidate();

                        totalMoves++;
                        break;
                    }
                }
                Log.i("[TileImageView.onTouch]", "Now, check whether you can swap two tiles!");
            }
            if (checkTiles()) {
                Log.i("end", "END GAME");
                totalTime = (System.currentTimeMillis() - startTime) / 1000;
                mActivity.totalTime.setText(Long.toString(totalTime));
                mActivity.totalMoves.setText(Integer.toString(totalMoves));

                mActivity.endGame();
            }
        }
        return true;
    }

    public boolean checkTiles() {
        for (int i = 0; i < gameTiles.size(); i++)
            if (!gameTiles.get(i).isCorrectPosition()) return false;
        return true;
    }

    public boolean gerResult() {
        return isWin;
    }

    /**
     * row, column 속성값이 변경될 때마다, Tile의 크기를 재계산한 다음, 다시 그리도록 요청한다.
     * 게임을 새로 시작하면서 행과 열의 값에 변경을 줄 경우 호출되어 사용됨.
     *
     * @param r 게임 화면에서 행(row)의 수
     * @param c 게임 화면에서 열(column)의 수
     */
    public void setDimension(int r, int c) {
        row = r;
        column = c;
        tileImageWidth = originalImage.getWidth() / column;
        tileImageHeight = originalImage.getHeight() / row;

        prepareTiles();  // 전체 Tile을 새로 작성하여 준비한다.
        invalidate();    // View의 내용이 변경되었으므로 다시 그릴 것을 Android System에 요청한다.
    }

    /**
     * Tile 객체들을 새로 생성하여 전체 Tile의 목록을 재작성한다.
     */
    private void prepareTiles() {
        // 기존 Tile들의 목록을 삭제한다.
        gameTiles.clear();

        Tile tile = null;
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        Rect sourceRect = new Rect(0, 0, 0, 0);
        Rect targetRect = new Rect(0, 0, tileImageWidth, tileImageHeight);
        for (int i = 0; i < row * column; i++) {  // 전체 Tile의 갯수 만큼 반복해서 처리함.
            int x = i / column;     // Tile의 행 위치
            int y = i % column;     // Tile의 열 위치

            // 빈 Tile 객체를 하나 생성해서, 원본 이미지의 해당 행과 열 위치의 이미지 조각을 Tile에 옮겨 그린다.
            tile = new Tile(x, y, Bitmap.createBitmap(tileImageWidth, tileImageHeight, Bitmap.Config.ARGB_8888));
            Canvas c = new Canvas(tile.image);
            sourceRect.left = y * tileImageWidth;
            sourceRect.top = x * tileImageHeight;
            sourceRect.right = (y + 1) * tileImageWidth;
            sourceRect.bottom = (x + 1) * tileImageHeight;
            c.drawBitmap(originalImage, sourceRect, targetRect, null);

            // 맨 마지막(오른쪽 아래 위치)의 Tile은 검은색 테두리를 쳐서 빈 공간을 나타내는 Tile로 사용한다.
            if (i == row * column - 1) {  // 가장 마지막 Tile(원본 이미지에서 오른쪽 아래 위치의 Tile)임.
                p.setStrokeWidth(30.0f);
                tile.isLastTile = true;
            } else {
                p.setStrokeWidth(2.0f);
                tile.isLastTile = false;
            }
            c.drawRect(0, 0, tileImageWidth, tileImageHeight, p);

            // 생성된 Tile 조각을 목록에 추가해 넣음
            gameTiles.add(tile);
        }

        Collections.shuffle(gameTiles);  // Tile의 순서를 무작위로 섞어 준다.

        // 섞인 상태에서 현재 화면에 보이는 행과 열의 위치를 계산해 둔다.
        for (int i = 0; i < row * column; i++) {
            tile = gameTiles.get(i);
            tile.current_x = i / column;
            tile.current_y = i % column;
        }
    }

    /**
     * 자신이 화면에 포함되어 사용되고 있는 MainActivity 객체에 대한 참조를 유지한다.
     *
     * @param activity 현재의 TileImageView 객체를 담고 있는 MainActivity 객체
     */
    public void setMain(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setFilterBitmap(true);

        // 전체 Tile을 그려 넣을 빈 Canvas를 준비한다.
        Point p = mActivity.getContentSize();
        Bitmap viewImage = Bitmap.createBitmap(p.x, p.y, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(viewImage);

        Rect sourceRect = new Rect(0, 0, tileImageWidth, tileImageHeight);
        Rect targetRect = new Rect(0, 0, 0, 0);
        for (int i = 0; i < gameTiles.size(); i++) { // 목록 내의 전체 Tile들에 대해서 반복처리한다.
            // 목록에서 i번째 Tile 조각을 화면상의 위치를 반영하여 준비해 놓은 Canvas에 그려준다.
            Tile tile = gameTiles.get(i);

            int imageWidthInScreen = p.x / column;
            int imageHeightInScreen = p.y / row;
            targetRect.left = tile.current_y * imageWidthInScreen;
            targetRect.top = tile.current_x * imageHeightInScreen;
            targetRect.right = (tile.current_y + 1) * imageWidthInScreen;
            targetRect.bottom = (tile.current_x + 1) * imageHeightInScreen;
            c.drawBitmap(gameTiles.get(i).image, sourceRect, targetRect, null);
        }

        // 모든 Tile이 그려진 Canvas를 TileImageView의 화면 영역에 옮겨 그려 넣는다.
        canvas.drawBitmap(viewImage, 0, 0, paint);

        // 화면에 그려 넣은 모든 Tile들이 제 위치로 왔는지 확인해서 게임이 종료되는 상황인지를 체크하여,
        // 후속 작업을 진행하도록 해야 한다!
    }
}
