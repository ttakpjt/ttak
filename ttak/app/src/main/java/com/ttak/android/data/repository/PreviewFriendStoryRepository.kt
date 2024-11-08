package com.ttak.android.data.repository

import com.ttak.android.domain.model.FriendStory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PreviewFriendStoryRepository : FriendStoryRepository {
    override fun getAllFriends(): Flow<List<FriendStory>> = flow {
        emit(listOf(
            FriendStory("2024111109382993714", "nickname111", "https://lh3.googleusercontent.com/a/ACg8ocJa4jTMizoM2OC6WKKrScXmHNFvrKftRC2mL4y55F\\_kp2nz\\_w=s96-c", true),
            FriendStory("2024110711443912527", "이규석", "https://picsum.photos/seed/hwang2/200", true),
            FriendStory("2024110800465017201", "merong", "https://picsum.photos/seed/kim3/200", true),
            FriendStory("4", "최싸피", "https://picsum.photos/seed/choi4/200", false),
            FriendStory("5", "박싸피", "https://picsum.photos/seed/park5/200", false),
            FriendStory("6", "이싸피", "https://picsum.photos/seed/lee6/200", false),
            FriendStory("7", "정싸피", "https://picsum.photos/seed/jung7/200", false),
            FriendStory("8", "강싸피", "https://picsum.photos/seed/kang8/200", false),
            FriendStory("9", "조싸피", "https://picsum.photos/seed/cho9/200", false),
            FriendStory("10", "윤싸피", "https://picsum.photos/seed/yoon10/200", false),
            FriendStory("11", "장싸피", "https://picsum.photos/seed/jang11/200", false),
            FriendStory("12", "임싸피", "https://picsum.photos/seed/lim12/200", false),
            FriendStory("13", "한싸피", "https://picsum.photos/seed/han13/200", false)
        ))
    }

    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> = flow {
        emit(listOf(
            FriendStory("1", "탁싸피", "https://picsum.photos/seed/ttak1/200", true),
            FriendStory("2024110800465017201", "merong", "https://picsum.photos/seed/kim3/200", true)
        ))
    }

    override suspend fun updateFriends(newFriends: List<FriendStory>) {
        // Preview에서는 아무 동작도 하지 않음
    }
}
//package com.ttak.android.data.repository
//
//import com.ttak.android.domain.model.FriendStory
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//
//class PreviewFriendStoryRepository : FriendStoryRepository {
//    override fun getAllFriends(): Flow<List<FriendStory>> = flow {
//        emit(listOf(
//            FriendStory("2024110711443912527", "eeee", "https://lh3.googleusercontent.com/a/ACg8ocJG71Pe8D64uw9eLkvOHYByjGdbMqsUkffbK9gTRqnQ5meviw=s96-c", true),
//            FriendStory("2", "황싸피", "https://picsum.photos/seed/hwang2/200", false),
//            FriendStory("3", "김싸피", "https://picsum.photos/seed/kim3/200", true),
//            FriendStory("4", "최싸피", "https://picsum.photos/seed/choi4/200", false),
//            FriendStory("5", "박싸피", "https://picsum.photos/seed/park5/200", false),
//            FriendStory("6", "이싸피", "https://picsum.photos/seed/lee6/200", false),
//            FriendStory("7", "정싸피", "https://picsum.photos/seed/jung7/200", false),
//            FriendStory("8", "강싸피", "https://picsum.photos/seed/kang8/200", false),
//            FriendStory("9", "조싸피", "https://picsum.photos/seed/cho9/200", false),
//            FriendStory("10", "윤싸피", "https://picsum.photos/seed/yoon10/200", false),
//            FriendStory("11", "장싸피", "https://picsum.photos/seed/jang11/200", false),
//            FriendStory("12", "임싸피", "https://picsum.photos/seed/lim12/200", false),
//            FriendStory("13", "한싸피", "https://picsum.photos/seed/han13/200", false)
//        ))
//    }
//
//    override fun getFriendsWithNewStories(): Flow<List<FriendStory>> = flow {
//        emit(listOf(
//            FriendStory("1", "탁싸피", "https://picsum.photos/seed/ttak1/200", true),
//            FriendStory("3", "김싸피", "https://picsum.photos/seed/kim3/200", true)
//        ))
//    }
//
//    override suspend fun updateFriends(newFriends: List<FriendStory>) {
//        // Preview에서는 아무 동작도 하지 않음
//    }
//}