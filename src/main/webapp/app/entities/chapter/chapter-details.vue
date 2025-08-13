<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="chapter">
        <h2 class="jh-entity-heading" data-cy="chapterDetailsHeading"><span>Chapter</span> {{ chapter.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Novel Id</span>
          </dt>
          <dd>
            <span>{{ chapter.novelId }}</span>
          </dd>
          <dt>
            <span>Chapter No</span>
          </dt>
          <dd>
            <span>{{ chapter.chapterNo }}</span>
          </dd>
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ chapter.title }}</span>
          </dd>
          <dt>
            <span>Content Id</span>
          </dt>
          <dd>
            <span>{{ chapter.contentId }}</span>
          </dd>
          <dt>
            <span>Word Count</span>
          </dt>
          <dd>
            <span>{{ chapter.wordCount }}</span>
          </dd>
          <dt>
            <span>Is Vip</span>
          </dt>
          <dd>
            <span>{{ chapter.isVip }}</span>
          </dd>
          <dt>
            <span>Price</span>
          </dt>
          <dd>
            <span>{{ chapter.price }}</span>
          </dd>
          <dt>
            <span>Create Time</span>
          </dt>
          <dd>
            <span v-if="chapter.createTime">{{ formatDateLong(chapter.createTime) }}</span>
          </dd>
          <dt>
            <span>Update Time</span>
          </dt>
          <dd>
            <span v-if="chapter.updateTime">{{ formatDateLong(chapter.updateTime) }}</span>
          </dd>
          <dt>
            <span>Novel</span>
          </dt>
          <dd>
            <div v-if="chapter.novel">
              <router-link :to="{ name: 'NovelView', params: { novelId: chapter.novel.id } }">{{ chapter.novel.title }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="chapter.id" :to="{ name: 'ChapterEdit', params: { chapterId: chapter.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./chapter-details.component.ts"></script>
