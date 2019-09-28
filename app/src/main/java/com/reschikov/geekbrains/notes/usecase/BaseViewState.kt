package com.reschikov.geekbrains.notes.usecase

open class BaseViewState<T> (val data: T, val error: Throwable?) {
}