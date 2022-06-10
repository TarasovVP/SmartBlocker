package com.tarasovvp.blacklister.ui.main.numberdetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants.BASE_URL
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.model.Categories
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.NumberInfo
import com.tarasovvp.blacklister.model.Ratings
import com.tarasovvp.blacklister.provider.ContactRepositoryImpl
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class NumberDetailViewModel(application: Application) : BaseViewModel(application) {

    private val contactRepository = ContactRepositoryImpl

    val numberDetailLiveData = MutableLiveData<Contact>()
    val numberInfoLiveData = MutableLiveData<NumberInfo>()

    fun getContact(number: String) {
        viewModelScope.launch {
            try {
                val contact = contactRepository.getContactByNumber(number) ?: Contact(name = number,
                    phone = number)
                numberDetailLiveData.postValue(contact)
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }

    fun getNumberInfo(number: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc: Document = Jsoup.connect(BASE_URL.plus(number)).get()
                val ratings = doc.select("div[class=ratings]")
                val categories = doc.select("div[class=categories]")
                val numberInfo = NumberInfo(ratings = Ratings(ratings.select("h2").text(),
                    ratings.select("li").map {
                        it.text().toString()
                    }),
                    categories = Categories(categories.select("h2").text(),
                        categories.select("li").map {
                            it.text().toString()
                        }))
                numberInfoLiveData.postValue(numberInfo)
            } catch (e: java.lang.Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}