CREATE INDEX idx_like_count_id ON study_room (like_count DESC, id DESC);

CREATE INDEX idx_review_count_id ON study_room (review_count DESC, id DESC);

CREATE INDEX idx_average_rating_id ON study_room (average_rating DESC, id DESC);

CREATE INDEX idx_entire_min_price_per_hour_id ON study_room (entire_min_price_per_hour ASC, id DESC);
