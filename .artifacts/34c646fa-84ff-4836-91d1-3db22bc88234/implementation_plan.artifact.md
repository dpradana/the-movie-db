# Implementation Plan - Add Error and Empty States

This plan outlines the steps to implement consistent UI states (Loading, Success, Empty, Error) across all features, ensuring robust handling of network failures, pagination errors, and cache fallbacks.

## Proposed Changes

### [core:common]

Introduce reusable string resources and layouts for error and empty states.

#### [MODIFY] [strings.xml](file:///Users/daniel/Work/the-movie-db/core/common/src/main/res/values/strings.xml)
- Add generic error and empty state strings.
- Add specific feature strings for empty states (e.g., "No genres found").

#### [NEW] [view_error_state.xml](file:///Users/daniel/Work/the-movie-db/core/common/src/main/res/layout/view_error_state.xml)
- A reusable layout containing a title, message, and retry button.

#### [NEW] [view_empty_state.xml](file:///Users/daniel/Work/the-movie-db/core/common/src/main/res/layout/view_empty_state.xml)
- A reusable layout containing a title and message.

---

### [feature:genre]

Update Genre feature to handle UI states correctly, including Room cache fallback.

#### [MODIFY] [GenreViewModel.kt](file:///Users/daniel/Work/the-movie-db/feature/genre/src/main/java/id/dpradana/themoviedb/feature/genre/presentation/GenreViewModel.kt)
- Ensure it handles `AppResult.Loading`, `AppResult.Success`, and `AppResult.Error`.
- Logic: If API fails but cache has data, show cache (Success). If both fail/empty, show Error.

#### [MODIFY] [fragment_genre.xml](file:///Users/daniel/Work/the-movie-db/feature/genre/src/main/res/layout/fragment_genre.xml)
- Replace inline error/empty views with `<include>` for the new reusable layouts.

#### [MODIFY] [GenreFragment.kt](file:///Users/daniel/Work/the-movie-db/feature/genre/src/main/java/id/dpradana/themoviedb/feature/genre/presentation/GenreFragment.kt)
- Update `renderState` to use the new reusable layout bindings.
- Wire the retry button from the included layout to the ViewModel.

---

### [feature:movie]

Update Movie feature for full-screen and pagination states.

#### [MODIFY] [MovieUiState.kt](file:///Users/daniel/Work/the-movie-db/feature/movie/src/main/java/id/dpradana/themoviedb/feature/movie/presentation/MovieUiState.kt)
- Use a sealed interface for distinct full-screen states (Loading, Success, Empty, Error) OR keep the data class but ensure clear flags for pagination errors. The requirement suggests "The UI state must be controlled by the ViewModel." A sealed interface is often cleaner for full-screen transitions.

#### [MODIFY] [MovieViewModel.kt](file:///Users/daniel/Work/the-movie-db/feature/movie/src/main/java/id/dpradana/themoviedb/feature/movie/presentation/MovieViewModel.kt)
- Implement page 1 vs. page 2+ error handling.
- Handle retry for both initial and pagination errors.

#### [MODIFY] [fragment_movie.xml](file:///Users/daniel/Work/the-movie-db/feature/movie/src/main/res/layout/fragment_movie.xml)
- Use `<include>` for error and empty states.

#### [MODIFY] [item_movie_footer.xml](file:///Users/daniel/Work/the-movie-db/feature/movie/src/main/res/layout/item_movie_footer.xml)
- Ensure it has a retry button for pagination errors.

#### [MODIFY] [MovieAdapter.kt](file:///Users/daniel/Work/the-movie-db/feature/movie/src/main/java/id/dpradana/themoviedb/feature/movie/presentation/MovieAdapter.kt)
- Update to handle a "Footer" state with error and retry.

---

### [feature:detail]

Update Movie Detail feature for Loading, Success, and Error states.

#### [MODIFY] [MovieDetailViewModel.kt](file:///Users/daniel/Work/the-movie-db/feature/detail/src/main/java/id/dpradana/themoviedb/feature/detail/presentation/MovieDetailViewModel.kt)
- Handle Loading, Success, and Error states via `MovieDetailUiState`.

#### [MODIFY] [fragment_movie_detail.xml](file:///Users/daniel/Work/the-movie-db/feature/detail/src/main/res/layout/fragment_movie_detail.xml)
- Add `<include>` for the error layout.

#### [MODIFY] [MovieDetailFragment.kt](file:///Users/daniel/Work/the-movie-db/feature/detail/src/main/java/id/dpradana/themoviedb/feature/detail/presentation/MovieDetailFragment.kt)
- Update UI rendering and retry logic.

---

### [feature:reviews]

Update Reviews feature with pagination error and empty states.

#### [MODIFY] [ReviewUiState.kt](file:///Users/daniel/Work/the-movie-db/feature/reviews/src/main/java/id/dpradana/themoviedb/feature/reviews/presentation/ReviewUiState.kt)
- Similar to MovieUiState, ensure clear handling of pagination and full-screen states.

#### [MODIFY] [ReviewViewModel.kt](file:///Users/daniel/Work/the-movie-db/feature/reviews/src/main/java/id/dpradana/themoviedb/feature/reviews/presentation/ReviewViewModel.kt)
- Handle initial vs. pagination failures.

#### [MODIFY] [fragment_reviews.xml](file:///Users/daniel/Work/the-movie-db/feature/reviews/src/main/res/layout/fragment_reviews.xml)
- Add `<include>` for empty and error layouts.

#### [MODIFY] [ReviewAdapter.kt](file:///Users/daniel/Work/the-movie-db/feature/reviews/src/main/java/id/dpradana/themoviedb/feature/reviews/presentation/ReviewAdapter.kt)
- Add footer support for pagination loading/error.

---

## Verification Plan

### Automated Tests
- Run updated ViewModel tests:
    - `./gradlew :feature:genre:test`
    - `./gradlew :feature:movie:test`
    - `./gradlew :feature:detail:test`
    - `./gradlew :feature:reviews:test`

### Manual Verification
- Deploy the app and test:
    - **Happy Path**: Check if data loads correctly in all features.
    - **Empty State**: Verify empty state shows when API returns no results.
    - **Initial Error**: Turn off internet and open the app to see the full-screen error and retry.
    - **Pagination Error**: Start scrolling, turn off internet, and verify the pagination footer error and retry.
    - **Cache Fallback**: Load genres, turn off internet, reopen app, and verify genres still show from cache.
