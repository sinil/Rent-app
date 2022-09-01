package com.riwal.rentalapp.common.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.core.firstKeyWithValueOrNull
import com.riwal.rentalapp.common.extensions.widgets.viewpager.onPageScrolled

class EasyViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {


    /*--------------------------------------- Properties -----------------------------------------*/


    var flag_refresh: Boolean = false
    var dataSource: DataSource? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var delegate: Delegate? = null

    var currentPage
        get() = currentItem
        set(value) {
            scrollToPageAtIndex(value)
        }

    private var lastPosition: Int = -1


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.EasyViewPager, 0, 0)
            isEnabled = styledAttributes.getBoolean(R.styleable.EasyViewPager_android_enabled, true)
            styledAttributes.recycle()
        }

        adapter = Adapter()

        onPageScrolled { position, positionOffset, _ ->
            val page = pageAt(position) ?: return@onPageScrolled
            if (positionOffset == 0f && lastPosition != position) {
                delegate?.onScrolledToPage(this, page, position)
                lastPosition = position
            }
        }
    }

    // TODO: Let this class extend from FrameLayout, use ViewPager as the only child view to hide
    // the currentItem setter to change pages. Create scrollToPage method instead of setting
    // currentItem so we can use onPageScrolled to derive page[Will|Did]Appear and
    // page[Will|Did]Disappear events

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?) = isEnabled && super.onTouchEvent(ev)

    override fun onInterceptTouchEvent(ev: MotionEvent?) = isEnabled && super.onInterceptTouchEvent(ev)


    /*----------------------------------------- Methods ------------------------------------------*/


    fun notifyDataSetChanged() {
        adapter!!.notifyDataSetChanged()
    }

    fun pageAt(position: Int) = (adapter as Adapter).pageAt(position)

    fun positionForPage(page: Page) = (adapter as Adapter).positionForPage(page)

    fun scrollToPageAtIndex(index: Int, animated: Boolean = true) {
        setCurrentItem(index, animated)
    }


    /*----------------------------------------- Classes ------------------------------------------*/


    open class Page(val view: View) {

        internal var parentViewPager: EasyViewPager? = null

        val adapterPosition
            get() = (parentViewPager?.adapter as Adapter).positionForPage(this)

    }

    private inner class Adapter : PagerAdapter() {

        private val positionsForPages: MutableMap<Page, Int> = mutableMapOf()
        private val lastKnownHashesForPages: MutableMap<Page, Long> = mutableMapOf()

        override fun isViewFromObject(view: View, `object`: Any) = view == (`object` as Page).view

        override fun getCount() = dataSource?.numberOfPages(this@EasyViewPager) ?: 0

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val inflater = LayoutInflater.from(context)
            val viewType = dataSource!!.pageTypeForPageAt(this@EasyViewPager, position)
            val view = dataSource!!.viewForPageType(this@EasyViewPager, viewType, container, inflater, position)
            val page = dataSource!!.pageForPageView(this@EasyViewPager, view, viewType)

            page.parentViewPager = this@EasyViewPager

            positionsForPages[page] = position

            val pageHash = dataSource!!.hashForPage(this@EasyViewPager, page)
            lastKnownHashesForPages[page] = pageHash

            container.addView(view)
            delegate?.onPageCreated(this@EasyViewPager, page, position)

            return page
        }

        override fun getItemPosition(`object`: Any): Int {
            val page = `object` as Page
            val hashForPage = try {
                dataSource!!.hashForPage(this@EasyViewPager, page)
            } catch (error: IndexOutOfBoundsException) { // DataSource's data can be changed at this point, so page.position can be invalid.
                null
            }
            // POSITION_NONE means that the page has changed so it need to refresh his page (instantiateItem will be called in this case).
            return  if(flag_refresh) POSITION_NONE else if (lastKnownHashForPage(page) == hashForPage) POSITION_UNCHANGED else POSITION_NONE
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val page = `object` as Page
            delegate?.onPageDestroy(this@EasyViewPager, page, position)
            page.parentViewPager = null
            positionsForPages.remove(page)
            lastKnownHashesForPages.remove(page)
            container.removeView(page.view)
        }

        fun positionForPage(page: Page) = positionsForPages[page] ?: POSITION_NONE

        fun pageAt(position: Int) = positionsForPages.firstKeyWithValueOrNull(position)

        fun lastKnownHashForPage(page: Page) = lastKnownHashesForPages[page]!!

    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface DataSource {
        fun numberOfPages(viewPager: EasyViewPager): Int
        fun pageTypeForPageAt(viewPager: EasyViewPager, position: Int): Int = 0
        fun viewForPageType(viewPager: EasyViewPager, viewType: Int, parent: ViewGroup, inflater: LayoutInflater, position: Int) = inflater.inflate(viewType, parent, false)!!
        fun pageForPageView(viewPager: EasyViewPager, view: View, viewType: Int) = Page(view)
        fun hashForPage(viewPager: EasyViewPager, page: Page): Long = 0
    }

    interface Delegate {
        fun onPageCreated(viewPager: EasyViewPager, page: Page, position: Int) {}
        fun onScrolledToPage(viewPager: EasyViewPager, page: Page, position: Int) {}
        fun onPageDestroy(viewPager: EasyViewPager, page: Page, position: Int) {}
    }

}
