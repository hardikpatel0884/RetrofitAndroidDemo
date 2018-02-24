package com.test.retrofit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.test.retrofit.R;
import com.test.retrofit.model.ModelUser;
import com.test.retrofit.utils.CircleTransform;

import java.util.List;

/**
 * Created by lcom151-one on 2/7/2018.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    List<ModelUser> users;
    UserClickInterface clickInterface;
    Context context;

    public UserAdapter(List<ModelUser> users, UserClickInterface clickInterface) {
        this.users = users;
        this.clickInterface = clickInterface;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, null, false));
    }

    @Override
    public void onBindViewHolder(UserHolder holder, final int position) {
        holder.tvName.setText(users.get(position).getName());
        String url = context.getResources().getString(R.string.host) + "user/" + users.get(position).getImage();
        /*Glide.with(context).load(url)
                .thumbnail(0.5f)
                .into(holder.ivProfile);*/

        Picasso.with(context).load(url).transform(new CircleTransform()).skipMemoryCache().into(holder.ivProfile);

        holder.llOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInterface.onClickUser(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvShow,tvDelete;
        ImageView ivProfile;
        LinearLayout llOption;

        public UserHolder(View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvName = itemView.findViewById(R.id.tv_name);
            llOption=itemView.findViewById(R.id.ll_show_detail);
            tvShow=itemView.findViewById(R.id.tv_show);
            tvDelete=itemView.findViewById(R.id.tv_delete);
        }
    }

    public interface UserClickInterface {
        void onClickUser(int position);
    }
}
