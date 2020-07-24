package com.amansiol.messenger.models;

public class PostModel {
   String pname;
   String puid;
   String pimage;
   String pdesc;
   String ptimestamp;
   String puserimage;
   String likes;
   String comments;

   public PostModel() {
   }

   public PostModel(String pname, String puid, String pimage, String pdesc, String ptimestamp, String puserimage, String likes, String comments) {
      this.pname = pname;
      this.puid = puid;
      this.pimage = pimage;
      this.pdesc = pdesc;
      this.ptimestamp = ptimestamp;
      this.puserimage = puserimage;
      this.likes = likes;
      this.comments = comments;
   }

   public String getPname() {
      return pname;
   }

   public void setPname(String pname) {
      this.pname = pname;
   }

   public String getPuid() {
      return puid;
   }

   public void setPuid(String puid) {
      this.puid = puid;
   }

   public String getPimage() {
      return pimage;
   }

   public void setPimage(String pimage) {
      this.pimage = pimage;
   }

   public String getPdesc() {
      return pdesc;
   }

   public void setPdesc(String pdesc) {
      this.pdesc = pdesc;
   }

   public String getPtimestamp() {
      return ptimestamp;
   }

   public void setPtimestamp(String ptimestamp) {
      this.ptimestamp = ptimestamp;
   }

   public String getPuserimage() {
      return puserimage;
   }

   public void setPuserimage(String puserimage) {
      this.puserimage = puserimage;
   }

   public String getLikes() {
      return likes;
   }

   public void setLikes(String likes) {
      this.likes = likes;
   }

   public String getComments() {
      return comments;
   }

   public void setComments(String comments) {
      this.comments = comments;
   }
}
