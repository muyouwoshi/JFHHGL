package com.juxin.predestinate.ui.live.ui;

import android.support.v4.app.Fragment;

import com.juxin.library.utils.InputUtils;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.ui.live.view.GestureLayout;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by terry on 2017/7/11.
 */

public class BaseLiveFragment extends Fragment {

    public CompositeDisposable rxDisposable = new CompositeDisposable();//订阅中心


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rxDisposable.size()>0){ //删除所有的rxbus对象
            rxDisposable.dispose();
        }
    }

    /**
     * 切换房间
     */
    public GestureLayout.PullToNextI onPullToNextListener = new GestureLayout.PullToNextI() {
        @Override
        public void previous() { //上一个

        }

        @Override
        public void next() {//下一个

        }

        @Override
        public void onUp(int clickY) {
            if (clickY >0){
                if (getActivity()!=null){
                    InputUtils.forceClose(getActivity());
                }
            }
            onTouchUp();
        }
    };

    public void onTouchUp(){

    }


}
