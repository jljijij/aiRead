<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiReadApp.chapterContent.home.createOrEditLabel" data-cy="ChapterContentCreateUpdateHeading">创建或编辑 Chapter Content</h2>
        <div>
          <div class="form-group" v-if="chapterContent.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="chapterContent.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-novelId">Novel Id</label>
            <input
              type="number"
              class="form-control"
              name="novelId"
              id="chapter-content-novelId"
              data-cy="novelId"
              :class="{ valid: !v$.novelId.$invalid, invalid: v$.novelId.$invalid }"
              v-model.number="v$.novelId.$model"
              required
            />
            <div v-if="v$.novelId.$anyDirty && v$.novelId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.novelId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-chapterId">Chapter Id</label>
            <input
              type="number"
              class="form-control"
              name="chapterId"
              id="chapter-content-chapterId"
              data-cy="chapterId"
              :class="{ valid: !v$.chapterId.$invalid, invalid: v$.chapterId.$invalid }"
              v-model.number="v$.chapterId.$model"
              required
            />
            <div v-if="v$.chapterId.$anyDirty && v$.chapterId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.chapterId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-pageId">Page Id</label>
            <input
              type="number"
              class="form-control"
              name="pageId"
              id="chapter-content-pageId"
              data-cy="pageId"
              :class="{ valid: !v$.pageId.$invalid, invalid: v$.pageId.$invalid }"
              v-model.number="v$.pageId.$model"
              required
            />
            <div v-if="v$.pageId.$anyDirty && v$.pageId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.pageId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-compressed">Compressed</label>
            <div>
              <div v-if="chapterContent.compressed" class="form-text text-danger clearfix">
                <span class="pull-left">{{ chapterContent.compressedContentType }}, {{ byteSize(chapterContent.compressed) }}</span>
                <button
                  type="button"
                  @click="
                    chapterContent.compressed = null;
                    chapterContent.compressedContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_compressed" class="btn btn-primary pull-right">增加 BLOB</label>
              <input
                type="file"
                ref="file_compressed"
                id="file_compressed"
                style="display: none"
                data-cy="compressed"
                @change="setFileData($event, chapterContent, 'compressed', false)"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="compressed"
              id="chapter-content-compressed"
              data-cy="compressed"
              :class="{ valid: !v$.compressed.$invalid, invalid: v$.compressed.$invalid }"
              v-model="v$.compressed.$model"
              required
            />
            <input
              type="hidden"
              class="form-control"
              name="compressedContentType"
              id="chapter-content-compressedContentType"
              v-model="chapterContent.compressedContentType"
            />
            <div v-if="v$.compressed.$anyDirty && v$.compressed.$invalid">
              <small class="form-text text-danger" v-for="error of v$.compressed.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-compressionType">Compression Type</label>
            <input
              type="number"
              class="form-control"
              name="compressionType"
              id="chapter-content-compressionType"
              data-cy="compressionType"
              :class="{ valid: !v$.compressionType.$invalid, invalid: v$.compressionType.$invalid }"
              v-model.number="v$.compressionType.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-hash">Hash</label>
            <input
              type="text"
              class="form-control"
              name="hash"
              id="chapter-content-hash"
              data-cy="hash"
              :class="{ valid: !v$.hash.$invalid, invalid: v$.hash.$invalid }"
              v-model="v$.hash.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-createTime">Create Time</label>
            <div class="d-flex">
              <input
                id="chapter-content-createTime"
                data-cy="createTime"
                type="datetime-local"
                class="form-control"
                name="createTime"
                :class="{ valid: !v$.createTime.$invalid, invalid: v$.createTime.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.createTime.$model)"
                @change="updateInstantField('createTime', $event)"
              />
            </div>
            <div v-if="v$.createTime.$anyDirty && v$.createTime.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createTime.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-content-chapter">Chapter</label>
            <select class="form-control" id="chapter-content-chapter" data-cy="chapter" name="chapter" v-model="chapterContent.chapter">
              <option :value="null"></option>
              <option
                :value="chapterContent.chapter && chapterOption.id === chapterContent.chapter.id ? chapterContent.chapter : chapterOption"
                v-for="chapterOption in chapters"
                :key="chapterOption.id"
              >
                {{ chapterOption.title }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>取消</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>保存</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./chapter-content-update.component.ts"></script>
