package com.tarasovvp.smartblocker.repositories

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY
import com.tarasovvp.smartblocker.UnitTestUtils.TEST_COUNTRY_CODE
import com.tarasovvp.smartblocker.data.database.dao.CountryCodeDao
import com.tarasovvp.smartblocker.data.repositoryImpl.CountryCodeRepositoryImpl
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.repository.CountryCodeRepository
import com.tarasovvp.smartblocker.utils.extensions.countryCodeList
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CountryCodeRepositoryTest {

    @MockK
    private lateinit var countryCodeDao: CountryCodeDao

    private lateinit var countryCodeRepository: CountryCodeRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        countryCodeRepository = CountryCodeRepositoryImpl(countryCodeDao)
    }

    @Test
    fun getSystemCountryCodeListTest() = runBlocking {
        val mockPhoneNumberUtil = mockk<PhoneNumberUtil>()
        val countryCodeMap = arrayListOf<CountryCode>()
        val region1 = "region1"
        val region2 = "region2"
        val countryCode1 = "+1"
        val countryCode2 = "+2"
        val numberFormat1 = "(123) 456-7890"
        val numberFormat2 = "(456) 789-0123"
        every { mockPhoneNumberUtil.supportedRegions } returns setOf(region1, region2)
        every { mockPhoneNumberUtil.getCountryCodeForRegion(region1) } returns 1
        every { mockPhoneNumberUtil.getCountryCodeForRegion(region2) } returns 2
        every { mockPhoneNumberUtil.format(any(), any()) } returnsMany listOf(numberFormat1, numberFormat2)
        every { mockPhoneNumberUtil.getExampleNumberForType(any(), any()) } returns mockk()
        every { mockPhoneNumberUtil.getExampleNumberForType(region1, PhoneNumberUtil.PhoneNumberType.MOBILE) } returns mockk()
        every { mockPhoneNumberUtil.getExampleNumberForType(region2, PhoneNumberUtil.PhoneNumberType.MOBILE) } returns mockk()
        val resultMock = mockk<(Int, Int) -> Unit>(relaxed = true)
        val countryCodeList = withContext(Dispatchers.Default) {
            mockPhoneNumberUtil.countryCodeList { size, position ->
                resultMock.invoke(size, position)
            }
        }
        countryCodeMap.add(CountryCode(region1, countryCode1, numberFormat1))
        countryCodeMap.add(CountryCode(region2, countryCode2, numberFormat2))
        assertEquals(countryCodeMap, countryCodeList)
        verify(exactly = 2) { resultMock.invoke(any(), any()) }
    }

    @Test
    fun insertAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        coEvery { countryCodeDao.insertAllCountryCode(countryCodeList) } just Runs
        countryCodeRepository.insertAllCountryCodes(countryCodeList)
        coVerify(exactly = 1) { countryCodeDao.insertAllCountryCode(countryCodeList) }
    }

    @Test
    fun getAllCountryCodesTest() = runBlocking {
        val countryCodeList = listOf(CountryCode(country = TEST_COUNTRY), CountryCode())
        coEvery { countryCodeDao.getAllCountryCodes() } returns countryCodeList
        val result = countryCodeRepository.getAllCountryCodes()
        assertEquals(countryCodeList, result)
    }

    @Test
    fun getCountryCodeWithCodeTest() = runBlocking {
        val countryCode = CountryCode(country = TEST_COUNTRY, countryCode = TEST_COUNTRY)
        coEvery { countryCodeDao.getCountryCodeWithCode(TEST_COUNTRY_CODE) } returns countryCode
        val result = countryCodeRepository.getCountryCodeWithCode(380)
        assertEquals(countryCode, result)
    }
}