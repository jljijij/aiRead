<template>
  <div>
    <h2 id="page-heading" data-cy="NovelTagHeading">
      <span id="novel-tag-heading">Novel Tags</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'NovelTagCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-novel-tag"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Novel Tag</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && novelTags && novelTags.length === 0">
      <span>No Novel Tags found</span>
    </div>
    <div class="table-responsive" v-if="novelTags && novelTags.length > 0">
      <table class="table table-striped" aria-describedby="novelTags">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('tagId')">
              <span>Tag Id</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'tagId'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('tagName')">
              <span>Tag Name</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'tagName'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('category')">
              <span>Category</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'category'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('isHot')">
              <span>Is Hot</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isHot'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createTime')">
              <span>Create Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createTime'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updateTime')">
              <span>Update Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updateTime'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="novelTag in novelTags" :key="novelTag.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'NovelTagView', params: { novelTagId: novelTag.id } }">{{ novelTag.id }}</router-link>
            </td>
            <td>{{ novelTag.tagId }}</td>
            <td>{{ novelTag.tagName }}</td>
            <td>{{ novelTag.category }}</td>
            <td>{{ novelTag.isHot }}</td>
            <td>{{ formatDateShort(novelTag.createTime) || '' }}</td>
            <td>{{ formatDateShort(novelTag.updateTime) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'NovelTagView', params: { novelTagId: novelTag.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'NovelTagEdit', params: { novelTagId: novelTag.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(novelTag)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">删除</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="aiReadApp.novelTag.delete.question" data-cy="novelTagDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-novelTag-heading">你确定要删除 Novel Tag {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-novelTag"
            data-cy="entityConfirmDeleteButton"
            @click="removeNovelTag()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="novelTags && novelTags.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./novel-tag.component.ts"></script>
