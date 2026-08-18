from typing import List
from pydantic import BaseModel


class ProfileItem(BaseModel):
    key: str
    value: str


class User(BaseModel):
    userid: int
    username: str
    organization: str
    role: List[str]
    profile: List[ProfileItem]