<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<footer class="main-footer">
  <div class="pull-right hidden-xs">
    <b>Version</b> 2.3.11
  </div>
    <strong>Copyright © 2016-2017 Worker's Market. All rights reserved </strong>

  <!-- Image Modal -->
  <div class="modal fade" id="postImgModel" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
          <div class="modal-content" style="width: 700px">
              <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                  </button>
                  <h4 class="modal-title" id="exampleModalLabel" style="margin-left: 40px;">投稿画像</h4>
              </div>
              <div class="modal-body" style="margin: auto">
                  <input type="hidden" id="imageFooter_PostImageList">
                  <input type="hidden" id="imageFooter_ImageIndex">
                  <a id="imageFooter_BackImage" class="inline-block " style="font-size: 50px; margin-right: 20px">&lt; </a>
                  <div class="row inline-block" style="width: 600px; text-align: center">
                      <img src="" id="post-img" class="post-img-zoom" style="margin: auto">
                  </div>
                  <a id="imageFooter_NextImage" class="inline-block" style="font-size: 50px; margin-left: 20px"> &gt;</a>
              </div>
              <div class="modal-footer" style="text-align: left">
                  <div class="info-image-post">
                      投稿名 : <span id="imageFooter_PostName"></span>
                  </div>
                  <div class="info-image-post">
                      投稿者ニックネーム : <span id="imageFooter_PostUserName"></span>
                  </div>
              </div>
          </div>
      </div>
  </div>

    <div class="modal fade" id="imgAvartarModel" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content" style="width: 600px">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body" style="margin: auto">
                    <div class="row inline-block" style="width: 600px; text-align: center">
                        <img src="" id="avatar-img" class="post-img-zoom" style="margin: auto">
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>