package com.example.systemdesignpracticeapps

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

/**
Share the code and design where you need to make  call Network call to get 10 images each time UI and  show image in a
form of list by its index  0 image, 1 image, 2 image.. etc

 data
- Api
- Repository

 domain
 -


 presentation

 */


// ---------------- Data ----------------

interface ImageApiService {
    @GET("images")
    suspend fun fetchImages(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<ImageDto>
}

data class ImageDto(
    val id: String,
    val url: String
)

fun ImageDto.toDomain() = ImageItem(id, url)

class ImagePagingSource(val api: ImageApiService): PagingSource<Int, ImageItem>() {
    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        val anchorPosition = state.anchorPosition
        if(anchorPosition!= null) {
            val anchorPage = state.closestPageToPosition(anchorPosition)
            return if(anchorPage?.prevKey != null) anchorPage.prevKey?.plus(1)
            else if(anchorPage?.nextKey != null) anchorPage.nextKey?.minus(1)
            else null
        } else return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        return try {
            val page = params.key ?: 1
            val response = api.fetchImages(page, 10).map{ it.toDomain() }
            LoadResult.Page(
                data = response,
                prevKey = if(page == 1) null else page-1,
                nextKey = if(response.isEmpty()) null else page+1
            )
        } catch(e: Exception) {
            LoadResult.Error(e)
        }
    }

}

class ImageRepositoryImpl @Inject constructor(val api: ImageApiService): ImageRepository {
    override fun fetchImages(): Flow<PagingData<ImageItem>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { ImagePagingSource(api) }
    ).flow
}


// ---------------- Domain ----------------

data class ImageItem(
    val id: String,
    val url: String
)

interface ImageRepository {
    fun fetchImages(): Pager<Int, ImageItem>
}


// ---------------- Presentation ----------------

@HiltViewModel
class ImageViewModel @Inject constructor(repository: ImageRepository): ViewModel() {
    val images = repository.fetchImages().flow.cachedIn()
}
















