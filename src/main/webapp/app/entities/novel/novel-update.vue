<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiReadApp.novel.home.createOrEditLabel" data-cy="NovelCreateUpdateHeading">创建或编辑 Novel</h2>
        <div>
          <div class="form-group" v-if="novel.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="novel.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="novel-title"
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
            <label class="form-control-label" for="novel-authorId">Author Id</label>
            <input
              type="number"
              class="form-control"
              name="authorId"
              id="novel-authorId"
              data-cy="authorId"
              :class="{ valid: !v$.authorId.$invalid, invalid: v$.authorId.$invalid }"
              v-model.number="v$.authorId.$model"
              required
            />
            <div v-if="v$.authorId.$anyDirty && v$.authorId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.authorId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-coverUrl">Cover Url</label>
            <input
              type="text"
              class="form-control"
              name="coverUrl"
              id="novel-coverUrl"
              data-cy="coverUrl"
              :class="{ valid: !v$.coverUrl.$invalid, invalid: v$.coverUrl.$invalid }"
              v-model="v$.coverUrl.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="novel-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-categoryId">Category Id</label>
            <input
              type="number"
              class="form-control"
              name="categoryId"
              id="novel-categoryId"
              data-cy="categoryId"
              :class="{ valid: !v$.categoryId.$invalid, invalid: v$.categoryId.$invalid }"
              v-model.number="v$.categoryId.$model"
              required
            />
            <div v-if="v$.categoryId.$anyDirty && v$.categoryId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.categoryId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tags">Tags</label>
            <input
              type="text"
              class="form-control"
              name="tags"
              id="novel-tags"
              data-cy="tags"
              :class="{ valid: !v$.tags.$invalid, invalid: v$.tags.$invalid }"
              v-model="v$.tags.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-wordCount">Word Count</label>
            <input
              type="number"
              class="form-control"
              name="wordCount"
              id="novel-wordCount"
              data-cy="wordCount"
              :class="{ valid: !v$.wordCount.$invalid, invalid: v$.wordCount.$invalid }"
              v-model.number="v$.wordCount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-chapterCount">Chapter Count</label>
            <input
              type="number"
              class="form-control"
              name="chapterCount"
              id="novel-chapterCount"
              data-cy="chapterCount"
              :class="{ valid: !v$.chapterCount.$invalid, invalid: v$.chapterCount.$invalid }"
              v-model.number="v$.chapterCount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-status">Status</label>
            <input
              type="number"
              class="form-control"
              name="status"
              id="novel-status"
              data-cy="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model.number="v$.status.$model"
              required
            />
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-isVip">Is Vip</label>
            <input
              type="checkbox"
              class="form-check"
              name="isVip"
              id="novel-isVip"
              data-cy="isVip"
              :class="{ valid: !v$.isVip.$invalid, invalid: v$.isVip.$invalid }"
              v-model="v$.isVip.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-createTime">Create Time</label>
            <div class="d-flex">
              <input
                id="novel-createTime"
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
            <label class="form-control-label" for="novel-updateTime">Update Time</label>
            <div class="d-flex">
              <input
                id="novel-updateTime"
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
<script lang="ts" src="./novel-update.component.ts"></script>
