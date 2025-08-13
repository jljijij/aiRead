<template>
  <div>
    <h2 id="page-heading" data-cy="ChapterHeading">
      <span id="chapter-heading">Chapters</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ChapterCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-chapter"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Chapter</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && chapters && chapters.length === 0">
      <span>No Chapters found</span>
    </div>
    <div class="table-responsive" v-if="chapters && chapters.length > 0">
      <table class="table table-striped" aria-describedby="chapters">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('novelId')">
              <span>Novel Id</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'novelId'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('chapterNo')">
              <span>Chapter No</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'chapterNo'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('title')">
              <span>Title</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('contentId')">
              <span>Content Id</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'contentId'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('wordCount')">
              <span>Word Count</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'wordCount'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('isVip')">
              <span>Is Vip</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'isVip'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('price')">
              <span>Price</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'price'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createTime')">
              <span>Create Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createTime'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('updateTime')">
              <span>Update Time</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updateTime'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('novel.title')">
              <span>Novel</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'novel.title'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="chapter in chapters" :key="chapter.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ChapterView', params: { chapterId: chapter.id } }">{{ chapter.id }}</router-link>
            </td>
            <td>{{ chapter.novelId }}</td>
            <td>{{ chapter.chapterNo }}</td>
            <td>{{ chapter.title }}</td>
            <td>{{ chapter.contentId }}</td>
            <td>{{ chapter.wordCount }}</td>
            <td>{{ chapter.isVip }}</td>
            <td>{{ chapter.price }}</td>
            <td>{{ formatDateShort(chapter.createTime) || '' }}</td>
            <td>{{ formatDateShort(chapter.updateTime) || '' }}</td>
            <td>
              <div v-if="chapter.novel">
                <router-link :to="{ name: 'NovelView', params: { novelId: chapter.novel.id } }">{{ chapter.novel.title }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ChapterView', params: { chapterId: chapter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ChapterEdit', params: { chapterId: chapter.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(chapter)"
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
        <span id="aiReadApp.chapter.delete.question" data-cy="chapterDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-chapter-heading">你确定要删除 Chapter {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-chapter"
            data-cy="entityConfirmDeleteButton"
            @click="removeChapter()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="chapters && chapters.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./chapter.component.ts"></script>
