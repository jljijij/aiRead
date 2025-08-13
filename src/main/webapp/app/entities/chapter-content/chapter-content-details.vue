<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="chapterContent">
        <h2 class="jh-entity-heading" data-cy="chapterContentDetailsHeading"><span>Chapter Content</span> {{ chapterContent.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Novel Id</span>
          </dt>
          <dd>
            <span>{{ chapterContent.novelId }}</span>
          </dd>
          <dt>
            <span>Chapter Id</span>
          </dt>
          <dd>
            <span>{{ chapterContent.chapterId }}</span>
          </dd>
          <dt>
            <span>Page Id</span>
          </dt>
          <dd>
            <span>{{ chapterContent.pageId }}</span>
          </dd>
          <dt>
            <span>Compressed</span>
          </dt>
          <dd>
            <span>{{ chapterContent.compressed }}</span>
          </dd>
          <dt>
            <span>Compression Type</span>
          </dt>
          <dd>
            <span>{{ chapterContent.compressionType }}</span>
          </dd>
          <dt>
            <span>Hash</span>
          </dt>
          <dd>
            <span>{{ chapterContent.hash }}</span>
          </dd>
          <dt>
            <span>Create Time</span>
          </dt>
          <dd>
            <span v-if="chapterContent.createTime">{{ formatDateLong(chapterContent.createTime) }}</span>
          </dd>
          <dt>
            <span>Chapter</span>
          </dt>
          <dd>
            <div v-if="chapterContent.chapter">
              <router-link :to="{ name: 'ChapterView', params: { chapterId: chapterContent.chapter.id } }">{{
                chapterContent.chapter.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link
          v-if="chapterContent.id"
          :to="{ name: 'ChapterContentEdit', params: { chapterContentId: chapterContent.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./chapter-content-details.component.ts"></script>
