<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="aiReadApp.novelTag.home.createOrEditLabel" data-cy="NovelTagCreateUpdateHeading">创建或编辑 Novel Tag</h2>
        <div>
          <div class="form-group" v-if="novelTag.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="novelTag.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tag-tagId">Tag Id</label>
            <input
              type="text"
              class="form-control"
              name="tagId"
              id="novel-tag-tagId"
              data-cy="tagId"
              :class="{ valid: !v$.tagId.$invalid, invalid: v$.tagId.$invalid }"
              v-model="v$.tagId.$model"
              required
            />
            <div v-if="v$.tagId.$anyDirty && v$.tagId.$invalid">
              <small class="form-text text-danger" v-for="error of v$.tagId.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tag-tagName">Tag Name</label>
            <input
              type="text"
              class="form-control"
              name="tagName"
              id="novel-tag-tagName"
              data-cy="tagName"
              :class="{ valid: !v$.tagName.$invalid, invalid: v$.tagName.$invalid }"
              v-model="v$.tagName.$model"
              required
            />
            <div v-if="v$.tagName.$anyDirty && v$.tagName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.tagName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tag-category">Category</label>
            <input
              type="text"
              class="form-control"
              name="category"
              id="novel-tag-category"
              data-cy="category"
              :class="{ valid: !v$.category.$invalid, invalid: v$.category.$invalid }"
              v-model="v$.category.$model"
              required
            />
            <div v-if="v$.category.$anyDirty && v$.category.$invalid">
              <small class="form-text text-danger" v-for="error of v$.category.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tag-isHot">Is Hot</label>
            <input
              type="checkbox"
              class="form-check"
              name="isHot"
              id="novel-tag-isHot"
              data-cy="isHot"
              :class="{ valid: !v$.isHot.$invalid, invalid: v$.isHot.$invalid }"
              v-model="v$.isHot.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="novel-tag-createTime">Create Time</label>
            <div class="d-flex">
              <input
                id="novel-tag-createTime"
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
            <label class="form-control-label" for="novel-tag-updateTime">Update Time</label>
            <div class="d-flex">
              <input
                id="novel-tag-updateTime"
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
<script lang="ts" src="./novel-tag-update.component.ts"></script>
