<template>
  <div>
    <h2 id="page-heading" data-cy="ChapterContentHeading">
      <span id="chapter-content-heading">Chapter Contents</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh list</span>
        </button>
        <router-link :to="{ name: 'ChapterContentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-chapter-content"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>创建新 Chapter Content</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && chapterContents && chapterContents.length === 0">
      <span>No Chapter Contents found</span>
    </div>
    <div class="table-responsive" v-if="chapterContents && chapterContents.length > 0">
      <table class="table table-striped" aria-describedby="chapterContents">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Novel Id</span></th>
            <th scope="row"><span>Chapter Id</span></th>
            <th scope="row"><span>Page Id</span></th>
            <th scope="row"><span>Compressed</span></th>
            <th scope="row"><span>Compression Type</span></th>
            <th scope="row"><span>Hash</span></th>
            <th scope="row"><span>Create Time</span></th>
            <th scope="row"><span>Chapter</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="chapterContent in chapterContents" :key="chapterContent.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ChapterContentView', params: { chapterContentId: chapterContent.id } }">{{
                chapterContent.id
              }}</router-link>
            </td>
            <td>{{ chapterContent.novelId }}</td>
            <td>{{ chapterContent.chapterId }}</td>
            <td>{{ chapterContent.pageId }}</td>
            <td>{{ chapterContent.compressed }}</td>
            <td>{{ chapterContent.compressionType }}</td>
            <td>{{ chapterContent.hash }}</td>
            <td>{{ formatDateShort(chapterContent.createTime) || '' }}</td>
            <td>
              <div v-if="chapterContent.chapter">
                <router-link :to="{ name: 'ChapterView', params: { chapterId: chapterContent.chapter.id } }">{{
                  chapterContent.chapter.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ChapterContentView', params: { chapterContentId: chapterContent.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">查看</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ChapterContentEdit', params: { chapterContentId: chapterContent.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">编辑</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(chapterContent)"
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
        <span id="aiReadApp.chapterContent.delete.question" data-cy="chapterContentDeleteDialogHeading">确认删除</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-chapterContent-heading">你确定要删除 Chapter Content {{ removeId }} 吗？</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">取消</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-chapterContent"
            data-cy="entityConfirmDeleteButton"
            @click="removeChapterContent()"
          >
            删除
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./chapter-content.component.ts"></script>
