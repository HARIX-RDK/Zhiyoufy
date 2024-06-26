package jsonplaceholder

type CreatePostReq struct {
	Title  string `json:"title"`
	Body   string `json:"body"`
	UserId int    `json:"userId"`
}

type UpdatePostReq struct {
	ID     int    `json:"id"`
	Title  string `json:"title"`
	Body   string `json:"body"`
	UserId int    `json:"userId"`
}
