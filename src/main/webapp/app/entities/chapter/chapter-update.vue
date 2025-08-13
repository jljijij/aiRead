<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiReadApp.chapter.home.createOrEditLabel" data-cy="ChapterCreateUpdateHeading">创建或编辑 Chapter</h2>
        <div>
          <div class="form-group" v-if="chapter.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="chapter.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-novelId">Novel Id</label>
            <input
              type="number"
              class="form-control"
              name="novelId"
              id="chapter-novelId"
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
            <label class="form-control-label" for="chapter-chapterNo">Chapter No</label>
            <input
              type="number"
              class="form-control"
              name="chapterNo"
              id="chapter-chapterNo"
              data-cy="chapterNo"
              :class="{ valid: !v$.chapterNo.$invalid, invalid: v$.chapterNo.$invalid }"
              v-model.number="v$.chapterNo.$model"
              required
            />
            <div v-if="v$.chapterNo.$anyDirty && v$.chapterNo.$invalid">
              <small class="form-text text-danger" v-for="error of v$.chapterNo.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="chapter-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-contentId">Content Id</label>
            <input
              type="number"
              class="form-control"
              name="contentId"
              id="chapter-contentId"
              data-cy="contentId"
              :class="{ valid: !v$.contentId.$invalid, invalid: v$.contentId.$invalid }"
              v-model.number="v$.contentId.$model"
              required
            />
            <div v-if="v$.contentId.$anyDirty && v$.contentId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.contentId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-wordCount">Word Count</label>
            <input
              type="number"
              class="form-control"
              name="wordCount"
              id="chapter-wordCount"
              data-cy="wordCount"
              :class="{ valid: !v$.wordCount.$invalid, invalid: v$.wordCount.$invalid }"
              v-model.number="v$.wordCount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-isVip">Is Vip</label>
            <input
              type="checkbox"
              class="form-check"
              name="isVip"
              id="chapter-isVip"
              data-cy="isVip"
              :class="{ valid: !v$.isVip.$invalid, invalid: v$.isVip.$invalid }"
              v-model="v$.isVip.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-price">Price</label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="chapter-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-createTime">Create Time</label>
            <div class="d-flex">
              <input
                id="chapter-createTime"
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
            <label class="form-control-label" for="chapter-updateTime">Update Time</label>
            <div class="d-flex">
              <input
                id="chapter-updateTime"
                data-cy="updateTime"
                type="datetime-local"
                class="form-control"
                name="updateTime"
                :class="{ valid: !v$.updateTime.$invalid, invalid: v$.updateTime.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.updateTime.$model)"
                @change="updateInstantField('updateTime', $event)"
              />
            </div>
            <div v-if="v$.updateTime.$anyDirty && v$.updateTime.$invalid">
              <small class="form-text text-danger" v-for="error of v$.updateTime.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="chapter-novel">Novel</label>
            <select class="form-control" id="chapter-novel" data-cy="novel" name="novel" v-model="chapter.novel">
              <option :value="null"></option>
              <option
                :value="chapter.novel && novelOption.id === chapter.novel.id ? chapter.novel : novelOption"
                v-for="novelOption in novels"
                :key="novelOption.id"
              >
                {{ novelOption.title }}
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
<script lang="ts" src="./chapter-update.component.ts"></script>
