<template>
  <div>
    <h2 id="page-heading" data-cy="NovelHeading">
      <span id="novel-heading">Novels</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'NovelCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-novel"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Novel</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && novels && novels.length === 0">
      <span>No Novels found</span>
    </div>
    <div class="table-responsive" v-if="novels && novels.length > 0">
      <table class="table table-striped" aria-describedby="novels">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Title</span></th>
            <th scope="row"><span>Author Id</span></th>
            <th scope="row"><span>Cover Url</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Category Id</span></th>
            <th scope="row"><span>Tags</span></th>
            <th scope="row"><span>Word Count</span></th>
            <th scope="row"><span>Chapter Count</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Is Vip</span></th>
            <th scope="row"><span>Create Time</span></th>
            <th scope="row"><span>Update Time</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="novel in novels" :key="novel.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'NovelView', params: { novelId: novel.id } }">{{ novel.id }}</router-link>
            </td>
            <td>{{ novel.title }}</td>
            <td>{{ novel.authorId }}</td>
            <td>{{ novel.coverUrl }}</td>
            <td>{{ novel.description }}</td>
            <td>{{ novel.categoryId }}</td>
            <td>{{ novel.tags }}</td>
            <td>{{ novel.wordCount }}</td>
            <td>{{ novel.chapterCount }}</td>
            <td>{{ novel.status }}</td>
            <td>{{ novel.isVip }}</td>
            <td>{{ formatDateShort(novel.createTime) || '' }}</td>
            <td>{{ formatDateShort(novel.updateTime) || '' }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'NovelView', params: { novelId: novel.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'NovelEdit', params: { novelId: novel.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(novel)"
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
        <span id="aiReadApp.novel.delete.question" data-cy="novelDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-novel-heading">你确定要删除 Novel {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-novel"
            data-cy="entityConfirmDeleteButton"
            @click="removeNovel()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./novel.component.ts"></script>
