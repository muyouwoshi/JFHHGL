package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.ui.live.view.GestureLayout;

/**
 * Created by terry on 2017/7/12.
 */

public class LiveEmptyFragment extends BaseLiveFragment {

    private ImageView mCloseIv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        final GestureLayout view = (GestureLayout) inflater.inflate(R.layout.l1_empty_view,container,false);

//        view.setIsEnableTouch(false);
        view.setPullToNextI(onPullToNextListener);
        view.setClickable(true);

        //用来处理视频开始 结束的操作 暂时注释
//        Disposable disposable = RxBus.getInstance().toFlowable(LiveBusEvent.class).subscribe(new Consumer<LiveBusEvent>() {
//            @Override
//            public void accept(@NonNull LiveBusEvent liveBusEvent) throws Exception {
//                if (liveBusEvent == LiveBusEvent.LIVE_PLAY_SUCCESS){
//                    Log.d("zt","SwitchRoomBusEvent----LiveEmptyFragment------LIVE_PLAY_SUCCESS");
//                    view.setIsEnableTouch(true);
//                }
//            }
//        });
//        rxDisposable.add(disposable);
//        Disposable disposable1 = RxBus.getInstance().toFlowable(SwitchRoomBusEvent.class).subscribe(new Consumer<SwitchRoomBusEvent>() {
//            @Override
//            public void accept(@NonNull SwitchRoomBusEvent livingBean) throws Exception {
//                Log.d("zt","SwitchRoomBusEvent----LiveEmptyFragment------"+livingBean.uid+"==="+livingBean.roomid);
//                view.setIsEnableTouch(false);
//            }
//        });
//        rxDisposable.add(disposable1);
        return  view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCloseIv = (ImageView) view.findViewById(R.id.live_empty_close);
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				Statistics.userBehavior(SendPoint.page_live_close);
//                ((H5LivePlayAct)getActivity()).liveClose();
            }
        });
    }
}
