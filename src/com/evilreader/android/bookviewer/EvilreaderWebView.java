package com.evilreader.android.bookviewer;
import com.evilreader.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import com.blahti.drag.DragController;
import com.blahti.drag.DragController.DragListener;
import com.blahti.drag.DragLayer;
import com.blahti.drag.DragSource;
import com.blahti.drag.MyAbsoluteLayout;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.View.OnLongClickListener;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

public class EvilreaderWebView extends WebView implements OnLongClickListener,
		OnTouchListener, DragListener, TextSelectionJavascriptInterfaceListener {

	/*=============================================
	 * 
	 * The fields of the customized webView control
	 * 
	 * 
	 * ==========================================*/
	private static final String TAG = "EvilreaderWebView";
	// Context.
	protected Context ctx;
	// The previously selected region.
	protected Region lastSelectedRegion = null;
	// The drag layer for selection.
	private DragLayer mSelectionDragLayer;	
	// The drag controller for selection.
	private DragController mDragController;	
	// The start selection handle.
	private ImageView mStartSelectionHandle;	
	// the end selection handle.
	private ImageView mEndSelectionHandle;
	// Identifier for the selection start handle.
	private final int SELECTION_START_HANDLE = 0;	
	// Identifier for the selection end handle.
	private final int SELECTION_END_HANDLE = 1;
	// Last touched selection handle.
	private int mLastTouchedSelectionHandle = -1;
	// The selection bounds.
	private Rect mSelectionBounds = null;
	// The selected range.
	protected String selectedRange = "";	
	// The selected text.
	protected String selectedText = "";
	// The current content width.
	protected int contentWidth = 0;
	
	private boolean mScrolling = false;
	private float mScrollDiffY = 0;
	private float mLastTouchY = 0;
	private float mScrollDiffX = 0;
	private float mLastTouchX = 0;
	private TextSelectionJavascriptInterface textSelectionJSInterface = null;
	
	/*=============================================
	 * 
	 * The constructors and the setups
	 * 
	 * 
	 * ==========================================*/

	public EvilreaderWebView(Context context) {
		super(context);

		this.ctx = context;
		this.setup(context);
	}

	public EvilreaderWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.ctx = context;
		this.setup(context);
	}

	public EvilreaderWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.ctx = context;
		this.setup(context);
	}

	protected void setup(Context context) {

		// On Touch Listener
		this.setOnLongClickListener(this);
		this.setOnTouchListener(this);

		// Webview setup
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginsEnabled(true);

		// Javascript interfaces
		 this.textSelectionJSInterface = new TextSelectionJavascriptInterface(context, this);
		 this.addJavascriptInterface(this.textSelectionJSInterface, this.textSelectionJSInterface.getInterfaceName());

		// Create the selection handles
		createSelectionLayer(context);

		// Set to the empty region
		Region region = new Region();
		region.setEmpty();
		this.lastSelectedRegion = region;
	}
	
	protected void createSelectionLayer(Context context) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mSelectionDragLayer = (DragLayer) inflater.inflate(
				R.layout.drag_layer, null);

		// Make sure it's filling parent
		this.mDragController = new DragController(context);
		this.mDragController.setDragListener(this);
		this.mDragController.addDropTarget(mSelectionDragLayer);
		this.mSelectionDragLayer.setDragController(mDragController);

		this.mStartSelectionHandle = (ImageView) this.mSelectionDragLayer
				.findViewById(R.id.Forward);
		this.mStartSelectionHandle.setTag(new Integer(SELECTION_START_HANDLE));
		this.mEndSelectionHandle = (ImageView) this.mSelectionDragLayer
				.findViewById(R.id.Back);
		this.mEndSelectionHandle.setTag(new Integer(SELECTION_END_HANDLE));

		OnTouchListener handleTouchListener = new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				boolean handledHere = false;

				final int action = event.getAction();

				// Down event starts drag for handle.
				if (action == MotionEvent.ACTION_DOWN) {
					handledHere = startDrag(v);
					mLastTouchedSelectionHandle = (Integer) v.getTag();
				}

				return handledHere;

			}

		};

		this.mStartSelectionHandle.setOnTouchListener(handleTouchListener);
		this.mEndSelectionHandle.setOnTouchListener(handleTouchListener);

	}

	public boolean onTouch(View v, MotionEvent event) {
		float xPoint = getDensityIndependentValue(event.getX(), ctx) / getDensityIndependentValue(this.getScale(), ctx);
		float yPoint = getDensityIndependentValue(event.getY(), ctx) / getDensityIndependentValue(this.getScale(), ctx);
		
		// TODO: Need to update this to use this.getScale() as a factor.
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			
			String startTouchUrl = String.format("javascript:android.selection.startTouch(%f, %f);", 
					xPoint, yPoint);
			
			mLastTouchX = xPoint;
			mLastTouchY = yPoint;
			
			this.loadUrl(startTouchUrl);
			
			// Flag scrolling for first touch
			//if(!this.isInSelectionMode())
				//mScrolling = true;
			
			
			
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			// Check for scrolling flag
			if(!mScrolling){
				this.endSelectionMode();
			}
			
			mScrollDiffX = 0;
			mScrollDiffY = 0;
			mScrolling = false;
			
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE){
			
			mScrollDiffX += (xPoint - mLastTouchX);
			mScrollDiffY += (yPoint - mLastTouchY);
			
			mLastTouchX = xPoint;
			mLastTouchY = yPoint;
			
			
			// Only account for legitimate movement.
			if(Math.abs(mScrollDiffX) > 10 || Math.abs(mScrollDiffY) > 10){
				mScrolling = true;
				
			}
			
			
		}
		
		// If this is in selection mode, then nothing else should handle this touch
		return false;
	}
	
	public boolean onLongClick(View v){
		
		this.loadUrl("javascript:android.selection.longTouch();");
		mScrolling = true;
		
		// Don't let the webview handle it
		return true;
	}

	
	/*=============================================
	 * 
	 * Handling the dragging on the Webview and 
	 * the javascript inside the webview	 * 
	 * 
	 * ==========================================*/
	
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub
		
	}

	
	public void onDragEnd() {
		// TODO Auto-generated method stub
		MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) this.mStartSelectionHandle.getLayoutParams();
		MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) this.mEndSelectionHandle.getLayoutParams();
		
		float scale = getDensityIndependentValue(this.getScale(), ctx);
		
		float startX = startHandleParams.x - this.getScrollX();
		float startY = startHandleParams.y - this.getScrollY();
		float endX = endHandleParams.x - this.getScrollX();
		float endY = endHandleParams.y - this.getScrollY();
		
		startX = getDensityIndependentValue(startX, ctx) / scale;
		startY = getDensityIndependentValue(startY, ctx) / scale;
		endX = getDensityIndependentValue(endX, ctx) / scale;
		endY = getDensityIndependentValue(endY, ctx) / scale;
		
		
		if(mLastTouchedSelectionHandle == SELECTION_START_HANDLE && startX > 0 && startY > 0){
			String saveStartString = String.format("javascript: android.selection.setStartPos(%f, %f);", startX, startY);
			this.loadUrl(saveStartString);
		}
			
		if(mLastTouchedSelectionHandle == SELECTION_END_HANDLE && endX > 0 && endY > 0){
			String saveEndString = String.format("javascript: android.selection.setEndPos(%f, %f);", endX, endY);
			this.loadUrl(saveEndString);
		}
	}
	
	public boolean startDrag(View v){
	    Object dragInfo = v;
	    mDragController.startDrag (v, mSelectionDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
		return true;
	}

	public float getDensityIndependentValue(float val, Context ctx){
		
		// Get display from context
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		// Calculate min bound based on metrics
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		
		return val / (metrics.densityDpi / 160f);
		
		//return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, val, metrics);
		
	}

	
	public float getDensityDependentValue(float val, Context ctx){
		
		// Get display from context
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		
		// Calculate min bound based on metrics
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		return val * (metrics.densityDpi / 160f);
		
		//return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, metrics);
		
	}
	
	/*=============================================
	 * 
	 * The implementation of the interface for updating
	 * the javascript on the web view. This is happening after
	 * the drag action on the WebView is performed	 * 
	 * 
	 * ==========================================*/
	
	public void tsjiJSError(String error) {
		Log.e(TAG, "JSError: " + error);
		
	}

	public void tsjiStartSelectionMode() {
		this.startSelectionMode();		
	}
	
	// Starts selection mode on the UI thread
	private Handler startSelectionModeHandler = new Handler(){
		public void handleMessage(Message m){
			if(mSelectionBounds == null)
				return;
			
			addView(mSelectionDragLayer);
			
			drawSelection();

			
			int contentHeight = (int) Math.ceil(getDensityDependentValue(getContentHeight(), ctx));
			
			// Update Layout Params
			ViewGroup.LayoutParams layerParams = mSelectionDragLayer.getLayoutParams();
			layerParams.height = contentHeight;
			layerParams.width = contentWidth;
			mSelectionDragLayer.setLayoutParams(layerParams);
		}		
	};
	public void startSelectionMode(){
		startSelectionModeHandler.sendEmptyMessage(0);
	}
	
	private Handler drawSelectionHandlesHandler = new Handler(){
		public void handleMessage(Message m){
			MyAbsoluteLayout.LayoutParams startParams = (com.blahti.drag.MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
			startParams.x = (int) (mSelectionBounds.left - mStartSelectionHandle.getDrawable().getIntrinsicWidth());
			startParams.y = (int) (mSelectionBounds.top - mStartSelectionHandle.getDrawable().getIntrinsicHeight());
		
			// Stay on screen.
			startParams.x = (startParams.x < 0) ? 0 : startParams.x;
			startParams.y = (startParams.y < 0) ? 0 : startParams.y;
			
			mStartSelectionHandle.setLayoutParams(startParams);
			
			MyAbsoluteLayout.LayoutParams endParams = (com.blahti.drag.MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
			endParams.x = (int) mSelectionBounds.right;
			endParams.y = (int) mSelectionBounds.bottom;
			
			// Stay on screen
			endParams.x = (endParams.x < 0) ? 0 : endParams.x;
			endParams.y = (endParams.y < 0) ? 0 : endParams.y;
			
			mEndSelectionHandle.setLayoutParams(endParams);
			}
	};
	public void drawSelection(){
		drawSelectionHandlesHandler.sendEmptyMessage(0);
	}
	
	// Ends selection mode on the UI thread
	private Handler endSelectionModeHandler = new Handler(){
			public void handleMessage(Message m){
			
				removeView(mSelectionDragLayer);
				/*if(getParent() != null && mContextMenu != null && contextMenuVisible){
					// This will throw an error if the webview is being redrawn.
					// No error handling needed, just need to stop the crash.
					try{
						mContextMenu.dismiss();
					}
					catch(Exception e){
						
					}
				}*/
				mSelectionBounds = null;
				mLastTouchedSelectionHandle = -1;
				loadUrl("javascript: android.selection.clearSelection();");
				
			}
		};
	public void endSelectionMode(){
		endSelectionModeHandler.sendEmptyMessage(0);
	}

	public void tsjiEndSelectionMode() {
		this.endSelectionMode();
	}
	

	public void tsjiSelectionChanged(String range, String text,
			String handleBounds, String menuBounds) {
		try {
			JSONObject selectionBoundsObject = new JSONObject(handleBounds);
			
			float scale = getDensityIndependentValue(this.getScale(), ctx);
			
			Rect handleRect = new Rect();
			handleRect.left = (int) (getDensityDependentValue(selectionBoundsObject.getInt("left"), getContext()) * scale);
			handleRect.top = (int) (getDensityDependentValue(selectionBoundsObject.getInt("top"), getContext()) * scale);
			handleRect.right = (int) (getDensityDependentValue(selectionBoundsObject.getInt("right"), getContext()) * scale);
			handleRect.bottom = (int) (getDensityDependentValue(selectionBoundsObject.getInt("bottom"), getContext()) * scale);
			
			this.mSelectionBounds = handleRect;
			this.selectedRange = range;
			this.selectedText = text;

			JSONObject menuBoundsObject = new JSONObject(menuBounds);
			
			Rect displayRect = new Rect();
			displayRect.left = (int) (getDensityDependentValue(menuBoundsObject.getInt("left"), getContext()) * scale);
			displayRect.top = (int) (getDensityDependentValue(menuBoundsObject.getInt("top") - 25, getContext()) * scale);
			displayRect.right = (int) (getDensityDependentValue(menuBoundsObject.getInt("right"), getContext()) * scale);
			displayRect.bottom = (int) (getDensityDependentValue(menuBoundsObject.getInt("bottom") + 25, getContext()) * scale);
			
			if(this.mSelectionDragLayer.getParent() == null){
				this.startSelectionMode();
			}
			
			// This will send the menu rect
			//this.showContextMenu(displayRect);
			
			drawSelection();
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public void tsjiSetContentWidth(float contentWidth) {
		this.contentWidth = (int) this.getDensityDependentValue(contentWidth, ctx);		
	}
}
