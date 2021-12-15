package map.together.items

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class ItemsList<Item>(var items: MutableList<Item>) {

    private val addingSubject: PublishSubject<Pair<Item, Int>> = PublishSubject.create()
    private val removingSubject: PublishSubject<Int> = PublishSubject.create()
    private val updatingSubject: PublishSubject<Int> = PublishSubject.create()
    private val updatingRangeSubject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()
    private val replacingSubject: PublishSubject<List<Item>> = PublishSubject.create()
    private val sizeChangedSubject: PublishSubject<Int> = PublishSubject.create()

    fun addingSubject(): Observable<Pair<Item, Int>> {
        return addingSubject
    }

    fun removingSubject(): Observable<Int> {
        return removingSubject
    }

    fun updatingSubject(): Observable<Int> {
        return updatingSubject
    }

    fun updatingRangeSubject(): Observable<Pair<Int, Int>> {
        return updatingRangeSubject
    }

    fun replacingSubject(): Observable<List<Item>> {
        return replacingSubject
    }

    fun sizeChangedSubject(): Observable<Int> {
        return sizeChangedSubject
    }

    fun add(item: Item) {
        items.add(0, item)
        sizeChangedSubject.onNext(items.size)
        addingSubject.onNext(Pair(item, 0))
    }

    fun addLast(item: Item) {
        val position = items.size
        items.add(position, item)
        sizeChangedSubject.onNext(items.size)
        addingSubject.onNext(Pair(item, position))
    }

    fun remove(item: Item) {
        val ind = items.indexOf(item)
        items.remove(item)
        sizeChangedSubject.onNext(items.size)
        removingSubject.onNext(ind)
    }

    open fun remove(position: Int) {
        items.removeAt(position)
        sizeChangedSubject.onNext(items.size)
        removingSubject.onNext(position)
    }

    fun update(position: Int, item: Item) {
        items[position] = item
        updatingSubject.onNext(position)
    }

    fun rangeUpdate(start: Int, count: Int) {
        updatingRangeSubject.onNext(Pair(start, count))
    }

    fun setData(data: MutableList<Item>) {
        items = data
        sizeChangedSubject.onNext(items.size)
        replacingSubject.onNext(items)
    }

    fun size(): Int {
        return items.size
    }

    fun contains(item: Item): Boolean {
        return items.contains(item)
    }

    fun indexOf(item: Item): Int {
        return items.indexOf(item)
    }
}
