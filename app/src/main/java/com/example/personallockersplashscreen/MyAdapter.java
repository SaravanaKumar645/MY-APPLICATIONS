package com.example.personallockersplashscreen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.example.personallockersplashscreen.UploadArea;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static androidx.core.content.ContextCompat.startActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "EXTENSION";
    Context context;
    int itemPos, itemposFilter;
    MyAdapter myAdapter;
    private FirebaseFirestore firestoreCloud;

    //RecyclerView mrecyclerView;
    private String userId, userPath;
    private List<String> fileList;
    List<String> filteredListfull;
    //public String FName;
    private String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOWNLOADS).getPath();


    public MyAdapter(Context context, String userId, List<String> list) {
        this.context = context;
        this.fileList = list;
        filteredListfull = new ArrayList<>(list);
        this.userId = userId;
        userPath = "user/" + userId + "/";

    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        itemPos = holder.getAdapterPosition();
        String fileDetails[]=fileList.get(position).split("@");
        final String fileName =fileDetails[0];
        final String fileUrl=fileDetails[1];
        //final String fileExtension=fileDetails[2];
        //final String mimeType= MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
        //final String fileNameFilter = filteredListfull.get(position);
        //FName=fileName;
        holder.nameofFile.setText(fileName);
        holder.nameofFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);



                browserIntent.setDataAndType(Uri.parse(fileUrl),getType(fileName));
                //browserIntent.addCategory("android.intent.category.LearnApps");
               // browserIntent.addCategory("android.intent.category.DEFAULT") ;
                //browserIntent.addCategory("android.intent.category.APP_FILES" );
                //browserIntent.addCategory("android.intent.category.ContentProviderApps");

                Intent chooser = Intent.createChooser(browserIntent, "Open With ...");
                //chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional
               /* if (chooser.resolveActivity() != null) {
                    context.startActivity(chooser);
                }*/
               context. startActivity(chooser);

            }
        });

        holder.mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(fileName);
            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile(fileName, itemPos);
            }
        });

    }

    private String getType(String fileName) {
        if ( fileName.contains(".docx")) {
            // Word document
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        else if ( fileName.contains(".doc")) {
            // Word document
            return "application/msword";
        }else if(fileName.contains(".pdf")) {
            // PDF file
            return "application/pdf";
        } else if(fileName.contains(".ppt") || fileName.contains(".pptx")) {
            // Powerpoint file
           // return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            return "application/vnd.ms-powerpoint";
            
        } else if(fileName.contains(".xls") || fileName.contains(".xlsx")) {
            // Excel file
            return "application/vnd.ms-excel";
        } else if(fileName.contains(".zip") || fileName.contains(".rar")) {
            // WAV audio file
            return "application/zip";
        } else if(fileName.contains(".rtf")) {
            // RTF file
            return "application/rtf";
        } else if(fileName.contains(".wav") || fileName.contains(".mp3")) {
            // WAV audio file
            return "audio/*";
        } else if(fileName.contains(".jpg") || fileName.contains(".jpeg") || fileName.contains(".png")||fileName.contains(".gif")) {
            // JPG file
            return "image/*";
        } else if(fileName.contains(".txt")) {
            // Text file
            return "text/plain";
        } else if(fileName.contains(".3gp") || fileName.contains(".mpg") || fileName.contains(".mpeg") || fileName.contains(".mkv") || fileName.contains(".mp4") ||fileName.contains(".avi")) {
            // Video files
            return  "video/*";
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            return "*/*";
        }
    }


    @Override
    public int getItemCount() {

        return fileList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            System.out.println("Done");

            List<String> filteredList = new ArrayList<>();
            if ((constraint == null || constraint.length() == 0)) {
                //fileList=new ArrayList<>(filteredListfull);
                filteredList.addAll(filteredListfull);

            } else {
                String FilterPattern = constraint.toString().toLowerCase().trim();
                for (String list1 : fileList) {

                    if (list1.toLowerCase().trim().contains(FilterPattern)) {

                        System.out.println(list1);
                        filteredList.add(list1);

                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;


        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            fileList.clear();
            fileList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
            //notifyItemRangeChanged(itemPos, fileList.size());
            //fileList=new ArrayList<>(filteredListfull);


        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameofFile;
        FloatingActionButton mDownload, mDelete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameofFile = itemView.findViewById(R.id.file_name_text_view);
            mDownload = itemView.findViewById(R.id.download_btn);
            mDelete = itemView.findViewById(R.id.delete_btn);


        }
    }

    private void downloadFile(String fileName) {
        String fileName1 = fileName;
        //String extension=getExtension(fileName);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference downloadRef = storageRef.child(userPath + fileName1);
        File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + (fileName1));

        downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("File RecyclerView", "downloaded the file");
                        Toast.makeText(context,
                                "Downloaded the file",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("File RecyclerView", "Failed to download the file");
                Toast.makeText(context, "Couldn't be downloaded", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void deleteFile(final String fileName,  int iPos) {
        Log.e(TAG, "deleteFile: "+iPos );
        int pos=iPos;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference deleteRef = storageRef.child(userPath + fileName);
        deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("File RecylerView", "delete the file");
                    Toast.makeText(context,
                            "File has been deleted",
                            Toast.LENGTH_SHORT).show();
                    deleteFileNameFromDB(fileName);
                    //fileList.remove(fileName);
                    fileList.remove(iPos);
                    filteredListfull.clear();
                    filteredListfull.addAll(fileList);
                    //filteredListfull.remove(iPos);
                    notifyItemRemoved(iPos);
                    notifyItemRangeChanged(iPos, fileList.size());
                    notifyDataSetChanged();
                   // fileList.clear();
                    //fileList.addAll(filteredListfull);
                    Log.e(TAG, "onComplete: "+filteredListfull );

                    //filteredListfull.removeAll(Collections.singleton(fileList.get(iPos)));
                    //fileList=new ArrayList<>(filteredListfull);



                } else {
                    Log.e("File RecylerView", "Failed to delete the file");
                    Toast.makeText(context,
                            "File Couldn't be deleted",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void deleteFileNameFromDB(String fileName) {

        firestoreCloud = FirebaseFirestore.getInstance();
        firestoreCloud.collection("UploadedFiles").document(userId + fileName).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("File RecylerView", "File name deleted from db");

                    }
                });
    }
}
