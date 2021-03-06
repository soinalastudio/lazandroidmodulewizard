package org.lamw.appjcentermikrotikrouterosdemo1;

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//-------------------------------------------------------------------------
//jImageBtn
//-------------------------------------------------------------------------

public class jImageBtn extends View {
	private Controls        controls = null;   // Control Class for Event
	private jCommons LAMWCommon;
	
	private Paint           mPaint   = null;
	private Bitmap          bmpUp    = null;
	private Bitmap          bmpDn    = null;
	private Bitmap          bmpUpAlpha = null;
	private Bitmap          bmpDnAlpha = null;
	private Rect            rect;
	private int             btnState = 0;      // Normal/Up = 0 , Pressed = 1
	private Boolean         enabled  = true;   //
	private int             sleep = 150;
	private int savedBackColor = Color.TRANSPARENT;

	//Constructor
	public jImageBtn(android.content.Context context,
					  Controls ctrls,long pasobj ) {
		super(context);
		controls = ctrls;
		LAMWCommon = new jCommons(this,context,pasobj);
		mPaint = new Paint();
		rect   = new Rect(0,0,200,200);
	}

	public void setButton(String fileup, String filedn) {
		setButtonUp(fileup);
		setButtonDown(filedn);
	}

	public void setButtonUp( String fileup) {
		// /data/data/com.example.appimagebtndemo1/files/btn_red.jpg
	    BitmapFactory.Options bo = new BitmapFactory.Options();
	    
	    if( bo != null ){
	     bo.inScaled = false;
	     
		 bmpUp = BitmapFactory.decodeFile(fileup,bo);
		 
		 if( bmpUp != null ){
		  rect = new Rect(0, 0, bmpUp.getWidth(), bmpUp.getHeight());
		  LAMWCommon.setLParamWidth(bmpUp.getWidth());
		  LAMWCommon.setLParamHeight(bmpUp.getHeight());
		  invalidate();
		 }
	    }
	}

	public void setButtonDown( String filedn ) {  
		// /data/data/com.example.appimagebtndemo1/files/btn_blue.jpg
	    BitmapFactory.Options bo = new BitmapFactory.Options();
	    
	    if( bo != null ){
	     bo.inScaled = false;
	     
	     bmpDn = BitmapFactory.decodeFile(filedn, bo);
	     
	     if( bmpDn != null ){		 
		  rect = new Rect(0, 0, bmpDn.getWidth(), bmpDn.getHeight());
		  invalidate();
	     }
	    }
	}

    private int GetDrawableResourceId(String _resName) {
		  try {
		     Class<?> res = R.drawable.class;
		     Field field = res.getField(_resName);  //"drawableName"
		     
		     if( field != null ){
		    	int drawableId = field.getInt(null);
		      	return drawableId;
		     } else
		    	 return 0;
		  }
		  catch (Exception e) {
		     //Log.e("GetDrawableResourceId", "Failure to get drawable id.", e);
		     return 0;
		  }
     }
    
     public Drawable GetDrawableResourceById(int _resID) {
				
		if( _resID == 0 ) return null;
		
		Drawable res = null;
		
		if (android.os.Build.VERSION.SDK_INT < 21 ) {
			res = this.controls.activity.getResources().getDrawable(_resID);
		}
		
		//[ifdef_api21up]
		if(android.os.Build.VERSION.SDK_INT >= 21)
			res = this.controls.activity.getResources().getDrawable(_resID, null);
        //[endif_api21up]
						
		return res;
	}
    
    /*
     *** It does not load the images correctly according to the resolution ***
     * 
     * public Bitmap GetBitmapResource(String _resourceDrawableIdentifier, boolean _inScaled) {
       int id =	GetDrawableResourceId(_resourceDrawableIdentifier);	
       BitmapFactory.Options bo = new BitmapFactory.Options();
       bo.inScaled = _inScaled; //false; 
       return  BitmapFactory.decodeResource(this.controls.activity.getResources(), id, bo);
    }*/    

	public  void setButtonUpByRes(String resup) {   // ..res/drawable
		//bmpUp = GetBitmapResource(resup, false);
		Drawable d = GetDrawableResourceById(GetDrawableResourceId(resup));
		
		if( d != null ){
		 bmpUp  = ((BitmapDrawable)d).getBitmap();
		 
		 if( bmpUp != null ){
		  rect   = new Rect(0,0,bmpUp.getWidth(),bmpUp.getHeight());
		  LAMWCommon.setLParamWidth(bmpUp.getWidth());
		  LAMWCommon.setLParamHeight(bmpUp.getHeight());
		  invalidate();
		 }
		}				
	}

	public  void setButtonDownByRes(String resdn) {   // ..res/drawable
		//bmpDn = bmpUp = GetBitmapResource(resdn, false);
		Drawable d = GetDrawableResourceById(GetDrawableResourceId(resdn));
		
		if( d != null ){
		 bmpDn  = ((BitmapDrawable)d).getBitmap();
		 
		 if(bmpDn != null){
		  rect   = new Rect(0,0,bmpDn.getWidth(),bmpDn.getHeight());		  
		  invalidate();
		 }
		}
				
	}
	
	public void SetImageDownScale( float _scale ) {
		
		if(bmpUp == null) return;
		
		int newWidth = (int)(bmpUp.getWidth()*_scale);
		int newHeight = (int)(bmpUp.getHeight()*_scale);
		
		Bitmap bmpScale = Bitmap.createScaledBitmap( bmpUp, newWidth, newHeight, true );
		
		if( bmpScale == null ) return;
		
		int posLeft = (bmpUp.getWidth() - bmpScale.getWidth()) / 2;
		int posTop  = (bmpUp.getHeight() - bmpScale.getHeight()) / 2;				
					
		bmpDn = Bitmap.createBitmap(bmpUp.getWidth(), bmpUp.getHeight(), Bitmap.Config.ARGB_8888);
						
		if( bmpDn != null ){			
			Canvas canvas = new Canvas(bmpDn);
			canvas.drawBitmap(bmpScale, posLeft, posTop, null);
			
			rect = new Rect(0, 0, bmpDn.getWidth(), bmpDn.getHeight());			
									
			invalidate();
		}
		
	}
	
	public void SetImageUpAlpha( int value ) {
		
		if( bmpUp == null ) return;
		
		if( value < 0 ) value = 0;
		if( value > 255) value = 255;
		
		if( bmpUpAlpha == null )
		 bmpUpAlpha = bmpUp;
		
	    int width  = bmpUp.getWidth();
	    int height = bmpUp.getHeight();
	    
	    bmpUp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    
	    if( bmpUp == null ) return;
	    
	    Canvas canvas = new Canvas(bmpUp);
	    
	    if( canvas != null ){
	     canvas.drawARGB(0, 0, 0, 0);
	    
	     // config paint
	     final Paint paint = new Paint();
	     paint.setAlpha(value);
	     canvas.drawBitmap(bmpUpAlpha, 0, 0, paint);
	    	     
	     rect = new Rect(0, 0, bmpUp.getWidth(), bmpUp.getHeight());		 
		 invalidate();
	    }
	}
	
    public void SetImageDnAlpha( int value ) {
		
		if( bmpDn == null ) return;
		
		if( value < 0 ) value = 0;
		if( value > 255) value = 255;
		
		if( bmpDnAlpha == null )
		 bmpDnAlpha = bmpDn;
		
	    int width  = bmpDn.getWidth();
	    int height = bmpDn.getHeight();
	    
	    bmpDn = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    
	    if( bmpDn == null ) return;
	    
	    Canvas canvas = new Canvas(bmpDn);
	    
	    if( canvas != null ){
	     canvas.drawARGB(0, 0, 0, 0);
	    
	     // config paint
	     final Paint paint = new Paint();
	     paint.setAlpha(value);
	     canvas.drawBitmap(bmpDnAlpha, 0, 0, paint);
	    	     
	     rect = new Rect(0, 0, bmpDn.getWidth(), bmpDn.getHeight());		 
		 invalidate();
	    }
	}
    
    public void SetAlpha( int value ){
    	SetImageUpAlpha(value);
    	SetImageDnAlpha(value);
    }

	//
	@Override
	public  boolean onTouchEvent( MotionEvent event) {
      //LORDMAN 2013-08-16
		if (enabled == false) { return false; }
		int actType = event.getAction()&MotionEvent.ACTION_MASK;
		switch(actType) {
			case MotionEvent.ACTION_DOWN: {  btnState = 1;
				//savedBackColor = GetBackgroundColor();
				//SetBackgroundColor(Color.YELLOW);
				invalidate();
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do something after: 1s = 1000ms
					}
				}, sleep);  //1s = 1000ms

				break;
			}
			case MotionEvent.ACTION_MOVE: // { break; } Fixed the bug: The button stays down after clicking and moving
			case MotionEvent.ACTION_UP  : {
				if ( btnState == 1 ){   //try fix twice event!
					btnState = 0;
					controls.pOnClick(LAMWCommon.getPasObj(), Const.Click_Default);
					//SetBackgroundColor(Color.GREEN);
					invalidate();
				}
				break;
			}
		}
		return true;
	}

	@Override
	public  void onDraw( Canvas canvas) {
		if (btnState == 0) {
			if (bmpUp != null) {
				LAMWCommon.setLParamWidth(bmpUp.getWidth());
				LAMWCommon.setLParamHeight(bmpUp.getHeight());				
				canvas.drawBitmap(bmpUp,null,rect,null);				
			}
		}
		else  {
			if (bmpDn != null) {
				LAMWCommon.setLParamWidth(bmpDn.getWidth());
				LAMWCommon.setLParamHeight(bmpDn.getHeight());
				canvas.drawBitmap(bmpDn,null,rect,null);
			}
		}
	}

	public void SetSleepDown(int _sleepMiliSeconds) {
        sleep = _sleepMiliSeconds;
	}

	public int GetBackgroundColor() {

		int c = Color.TRANSPARENT;
		Drawable background = this.getBackground();
		if (background instanceof ColorDrawable) {
			c = ((ColorDrawable)this.getBackground()).getColor();
		} else {
			//if (mIsRounded = true) c = mBackgroundColor;
		}

		return c;
	}

	public void SetBackgroundColor(int _color) {
		if  (this != null) {
			//mBackgroundColor = _color;
			this.setBackgroundColor(_color);
			//this.setAlpha(0.5f);
		}
	}

	public  void setEnabled(boolean value) {
		enabled = value;
	}

	public  void Free() {
		if (bmpUp  != null) { bmpUp.recycle();         }
		if (bmpDn  != null) { bmpDn.recycle();         }
		LAMWCommon.free();
		bmpUp   = null;
		bmpDn   = null;
		mPaint  = null;
		rect    = null;
	}

	public long GetPasObj() {
		return LAMWCommon.getPasObj();
	}

	public  void setParent(ViewGroup _viewgroup ) {
		LAMWCommon.setParent(_viewgroup);
	}
	
	public ViewGroup GetParent() {
		return LAMWCommon.getParent();
	}
	
	public void RemoveFromViewParent() {
		LAMWCommon.removeFromViewParent();
	}

	public void setLeftTopRightBottomWidthHeight(int left, int top, int right, int bottom, int w, int h) {
		LAMWCommon.setLeftTopRightBottomWidthHeight(left,top,right,bottom,w,h);
	}
		
	public void setLParamWidth(int w) {
		LAMWCommon.setLParamWidth(w);
	}

	public void setLParamHeight(int h) {
		LAMWCommon.setLParamHeight(h);
	}
    
	public int GetLParamHeight() {
		return  LAMWCommon.getLParamHeight();
	}

	public int GetLParamWidth() {				
		return LAMWCommon.getLParamWidth();					
	}  

	public void setLGravity(int _g) {
		LAMWCommon.setLGravity(_g);
	}

	public void setLWeight(float _w) {
		LAMWCommon.setLWeight(_w);
	}

	public void addLParamsAnchorRule(int rule) {
		LAMWCommon.addLParamsAnchorRule(rule);
	}
	
	public void addLParamsParentRule(int rule) {
		LAMWCommon.addLParamsParentRule(rule);
	}

	public void setLayoutAll(int idAnchor) {
		LAMWCommon.setLayoutAll(idAnchor);
	}
	
	public void ClearLayoutAll() {		
		LAMWCommon.clearLayoutAll();
	}
}

